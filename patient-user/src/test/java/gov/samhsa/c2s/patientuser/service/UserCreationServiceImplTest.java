package gov.samhsa.c2s.patientuser.service;

import gov.samhsa.c2s.patientuser.config.EmailSenderProperties;
import gov.samhsa.c2s.patientuser.domain.*;
import gov.samhsa.c2s.patientuser.infrastructure.EmailSender;
import gov.samhsa.c2s.patientuser.infrastructure.PhrService;
import gov.samhsa.c2s.patientuser.infrastructure.ScimService;
import gov.samhsa.c2s.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.c2s.patientuser.service.dto.UserActivationRequestDto;
import gov.samhsa.c2s.patientuser.service.dto.UserActivationResponseDto;
import gov.samhsa.c2s.patientuser.service.dto.UserCreationRequestDto;
import gov.samhsa.c2s.patientuser.service.dto.UserCreationResponseDto;
import gov.samhsa.c2s.patientuser.service.exception.*;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static gov.samhsa.c2s.common.unit.matcher.ArgumentMatchers.matching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserCreationServiceImplTest {

    private final String xForwardedProto = "https";
    private final String xForwardedHost = "xforwardedhost";
    private final int xForwardedPort = 443;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private UserCreationRepository userCreationRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    @Mock
    private EmailTokenGenerator emailTokenGenerator;

    @Mock
    private EmailSender emailSender;

    @Mock
    private PhrService phrService;

    @Mock
    private ScimService scimService;

    @Mock
    private EmailSenderProperties emailSenderPropertiesMock;

    private int emailTokenExpirationInDays = 7;

    @InjectMocks
    private UserCreationServiceImpl sut;

    @Before
    public void setup() {
        when(emailSenderPropertiesMock.getEmailTokenExpirationInDays()).thenReturn(emailTokenExpirationInDays);
    }

    @Test
    public void testInitiateUserCreation_Create_New_UserCreation() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final String email = "email";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String emailToken = "emailToken";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final Date birthDate = new Date(year, month, day);
        final UserCreationRequestDto userCreationRequestDto = mock(UserCreationRequestDto.class);
        when(userCreationRequestDto.getPatientId()).thenReturn(patientId);
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrService.findPatientProfileById(patientId)).thenReturn(patientDto);
        when(patientDto.getEmail()).thenReturn(email);
        when(patientDto.getBirthDate()).thenReturn(birthDate);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getId()).thenReturn(patientId);
        final UserType userType = mock(UserType.class);
        when(userTypeRepository.findOneByType(UserTypeEnum.SELF)).thenReturn(Optional.of(userType));
        when(emailTokenGenerator.generateEmailToken()).thenReturn(emailToken);
        when(userCreationRepository.findOneByPatientId(patientId)).thenReturn(Optional.empty());
        when(tokenGenerator.generateToken(7)).thenReturn(verificationCode);
        final UserCreationResponseDto userCreationResponseDto = mock(UserCreationResponseDto.class);
        when(modelMapper.map(patientDto, UserCreationResponseDto.class)).thenReturn(userCreationResponseDto);
        final UserCreation savedUserCreation = mock(UserCreation.class);
        when(savedUserCreation.getVerificationCode()).thenReturn(verificationCode);
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(emailTokenExpirationInDays));
        when(savedUserCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(savedUserCreation.getEmailToken()).thenReturn(emailToken);
        when(userCreationRepository.save(any(UserCreation.class))).thenReturn(savedUserCreation);

        // Act
        final UserCreationResponseDto response = sut.initiateUserCreation(userCreationRequestDto, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userCreationResponseDto, response);
        verify(userCreationRepository, times(1)).save(argThat(matching(
                (UserCreation uc) -> uc.getPatientId().equals(patientId) &&
                        uc.getUserType().equals(userType) &&
                        uc.getEmailToken().equals(emailToken) &&
                        uc.getEmailTokenExpirationAsInstant().isAfter(Instant.now()) &&
                        uc.getUserId() == null &&
                        uc.getVerificationCode().equals(verificationCode))
        ));
        verify(userCreationResponseDto, times(1)).setVerificationCode(verificationCode);
        verify(userCreationResponseDto, times(1)).setEmailTokenExpiration(emailTokenExpiration);
        verify(emailSender, times(1)).sendEmailWithVerificationLink(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email),
                eq(emailToken),
                argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testInitiateUserCreation_Update_Existing_UserCreation_Throws_UserIsAlreadyVerifiedException() throws Exception {
        // Arrange
        thrown.expect(UserIsAlreadyVerifiedException.class);
        final Long patientId = 10L;
        final String email = "email";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String emailToken = "emailToken";
        final String verificationCode = "verificationCode";
        final UserCreationRequestDto userCreationRequestDto = mock(UserCreationRequestDto.class);
        when(userCreationRequestDto.getPatientId()).thenReturn(patientId);
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrService.findPatientProfileById(patientId)).thenReturn(patientDto);
        when(patientDto.getEmail()).thenReturn(email);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getId()).thenReturn(patientId);
        final UserType userType = mock(UserType.class);
        when(userTypeRepository.findOneByType(UserTypeEnum.SELF)).thenReturn(Optional.of(userType));
        when(emailTokenGenerator.generateEmailToken()).thenReturn(emailToken);
        UserCreation existingUserCreation = mock(UserCreation.class);
        when(existingUserCreation.isVerified()).thenReturn(true);
        when(userCreationRepository.findOneByPatientId(patientId)).thenReturn(Optional.of(existingUserCreation));

        // Act
        final UserCreationResponseDto response = sut.initiateUserCreation(userCreationRequestDto, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        verify(userCreationRepository, times(1)).findOneByPatientId(patientId);
        assertNull(response);
    }

    @Test
    public void testInitiateUserCreation_Update_Existing_UserCreation() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final String email = "email";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String emailToken = "emailToken";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final Date birthDate = new Date(year, month, day);
        final UserCreationRequestDto userCreationRequestDto = mock(UserCreationRequestDto.class);
        when(userCreationRequestDto.getPatientId()).thenReturn(patientId);
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrService.findPatientProfileById(patientId)).thenReturn(patientDto);
        when(patientDto.getEmail()).thenReturn(email);
        when(patientDto.getBirthDate()).thenReturn(birthDate);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getId()).thenReturn(patientId);
        final UserType userType = mock(UserType.class);
        when(userTypeRepository.findOneByType(UserTypeEnum.SELF)).thenReturn(Optional.of(userType));
        when(emailTokenGenerator.generateEmailToken()).thenReturn(emailToken);
        UserCreation existingUserCreation = mock(UserCreation.class);
        when(existingUserCreation.isVerified()).thenReturn(false);
        when(userCreationRepository.findOneByPatientId(patientId)).thenReturn(Optional.of(existingUserCreation));
        when(tokenGenerator.generateToken(7)).thenReturn(verificationCode);
        final UserCreationResponseDto userCreationResponseDto = mock(UserCreationResponseDto.class);
        when(modelMapper.map(patientDto, UserCreationResponseDto.class)).thenReturn(userCreationResponseDto);
        final UserCreation savedUserCreation = mock(UserCreation.class);
        when(savedUserCreation.getVerificationCode()).thenReturn(verificationCode);
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(emailTokenExpirationInDays));
        when(savedUserCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(savedUserCreation.getEmailToken()).thenReturn(emailToken);
        when(userCreationRepository.save(any(UserCreation.class))).thenReturn(savedUserCreation);

        // Act
        final UserCreationResponseDto response = sut.initiateUserCreation(userCreationRequestDto, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userCreationResponseDto, response);
        verify(userCreationRepository, times(1)).save(existingUserCreation);
        verify(userCreationResponseDto, times(1)).setVerificationCode(verificationCode);
        verify(userCreationResponseDto, times(1)).setEmailTokenExpiration(emailTokenExpiration);
        verify(emailSender, times(1)).sendEmailWithVerificationLink(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email),
                eq(emailToken),
                argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
        verify(existingUserCreation, times(1)).setEmailTokenExpirationAsInstant(argThat(matching(instant -> instant.isAfter(Instant.now()))));
        verify(existingUserCreation, times(1)).setEmailToken(emailToken);
        verify(existingUserCreation, times(1)).setPatientId(patientId);
        verify(existingUserCreation, times(1)).setUserType(userType);
        verify(existingUserCreation, times(1)).setVerified(false);
        verify(existingUserCreation, times(1)).setVerificationCode(verificationCode);
    }

    @Test
    public void testFindUserCreationInfoByPatientId() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final Date birthDate = new Date(year, month, day);
        final Instant emailTokenExpiration = Instant.now();
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrService.findPatientProfileById(patientId)).thenReturn(patientDto);
        when(patientDto.getBirthDate()).thenReturn(birthDate);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreationRepository.findOneByPatientId(patientId)).thenReturn(Optional.of(userCreation));
        when(userCreation.getVerificationCode()).thenReturn(verificationCode);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        final UserCreationResponseDto userCreationResponseDto = mock(UserCreationResponseDto.class);
        when(modelMapper.map(patientDto, UserCreationResponseDto.class)).thenReturn(userCreationResponseDto);

        // Act
        final UserCreationResponseDto response = sut.findUserCreationInfoByPatientId(patientId);

        // Assert
        assertEquals(userCreationResponseDto, response);
        verify(userCreationResponseDto, times(1)).setVerificationCode(verificationCode);
        verify(userCreationResponseDto, times(1)).setEmailTokenExpiration(emailTokenExpiration);
    }

    @Test
    public void testFindUserCreationInfoByPatientId_Throws_UserCreationNotFoundException_When_Not_Found() throws Exception {
        // Arrange
        thrown.expect(UserCreationNotFoundException.class);
        final Long patientId = 10L;
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrService.findPatientProfileById(patientId)).thenReturn(patientDto);
        when(userCreationRepository.findOneByPatientId(patientId)).thenReturn(Optional.empty());

        // Act
        final UserCreationResponseDto response = sut.findUserCreationInfoByPatientId(patientId);

        // Assert
        assertNull(response);
    }

    @Test
    public void testActivateUser() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final Boolean initialVerified = false;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String username = "email";
        final String password = "password";
        final String confirmPassword = "password";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        when(userActivationRequest.getUsername()).thenReturn(username);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.of(userCreation));
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testActivateUser_Throws_UserActivationCannotBeVerifiedException_When_Username_Does_Not_Match_Email_In_PHR() throws Exception {
        // Arrange
        thrown.expect(UserActivationCannotBeVerifiedException.class);
        final Long patientId = 10L;
        final Boolean initialVerified = false;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String username = "emailDifferent";
        final String password = "password";
        final String confirmPassword = "password";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        when(userActivationRequest.getUsername()).thenReturn(username);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.of(userCreation));
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testActivateUser_Throws_PasswordConfirmationFailedException_When_Password_Confirmation_Fails() throws Exception {
        // Arrange
        thrown.expect(PasswordConfirmationFailedException.class);
        final Long patientId = 10L;
        final Boolean initialVerified = false;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String password = "password";
        final String confirmPassword = "confirmPassword";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.of(userCreation));
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testActivateUser_Throws_UserActivationCannotBeVerifiedException_When_UserCreation_Not_Found() throws Exception {
        // Arrange
        thrown.expect(UserActivationCannotBeVerifiedException.class);
        final Long patientId = 10L;
        final Boolean initialVerified = false;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String password = "password";
        final String confirmPassword = "password";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.empty());
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testActivateUser_Throws_UserIsAlreadyVerifiedException_When_User_Is_Already_Verified() throws Exception {
        // Arrange
        thrown.expect(UserIsAlreadyVerifiedException.class);
        final Long patientId = 10L;
        final Boolean initialVerified = true;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String password = "password";
        final String confirmPassword = "password";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.of(userCreation));
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testActivateUser_Throws_EmailTokenExpiredException_When_Email_Token_Already_Expired() throws Exception {
        // Arrange
        thrown.expect(EmailTokenExpiredException.class);
        final Long patientId = 10L;
        final Boolean initialVerified = false;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().minus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String password = "password";
        final String confirmPassword = "password";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.of(userCreation));
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }

    @Test
    public void testActivateUser_Throws_UserActivationCannotBeVerifiedException_When_Birth_Date_Verification_Failed() throws Exception {
        // Arrange
        thrown.expect(UserActivationCannotBeVerifiedException.class);
        final Long patientId = 10L;
        final Boolean initialVerified = false;
        final Boolean verified = true;
        final Instant emailTokenExpiration = Instant.now().plus(Period.ofDays(7));
        final String verificationCode = "verificationCode";
        final String emailToken = "emailToken";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String email = "email";
        final String password = "password";
        final String confirmPassword = "password";
        final String userId = "userId";
        final LocalDate birthDateInRequest = LocalDate.of(1980, 1, 1);
        final Date birthDateInPhr = Date.from(birthDateInRequest.minus(Period.ofDays(1)).atStartOfDay(ZoneId.systemDefault()).toInstant());
        UserActivationRequestDto userActivationRequest = mock(UserActivationRequestDto.class);
        when(userActivationRequest.getVerificationCode()).thenReturn(verificationCode);
        when(userActivationRequest.getEmailToken()).thenReturn(emailToken);
        when(userActivationRequest.getBirthDate()).thenReturn(birthDateInRequest);
        when(userActivationRequest.getPassword()).thenReturn(password);
        when(userActivationRequest.getConfirmPassword()).thenReturn(confirmPassword);
        final UserCreation userCreation = mock(UserCreation.class);
        when(userCreation.isVerified()).thenReturn(initialVerified).thenReturn(verified);
        when(userCreation.getEmailTokenExpirationAsInstant()).thenReturn(emailTokenExpiration);
        when(userCreation.getPatientId()).thenReturn(patientId);
        when(userCreationRepository.findOneByEmailTokenAndVerificationCode(emailToken, verificationCode)).thenReturn(Optional.of(userCreation));
        final PatientDto patientDto = mock(PatientDto.class);
        when(patientDto.getBirthDate()).thenReturn(birthDateInPhr);
        when(patientDto.getFirstName()).thenReturn(firstName);
        when(patientDto.getLastName()).thenReturn(lastName);
        when(patientDto.getEmail()).thenReturn(email);
        when(phrService.findPatientProfileById(patientId, true)).thenReturn(patientDto);
        final UserActivationResponseDto userActivationResponse = mock(UserActivationResponseDto.class);
        when(modelMapper.map(patientDto, UserActivationResponseDto.class)).thenReturn(userActivationResponse);
        final ScimUser savedScimUser = new ScimUser();
        savedScimUser.setId(userId);
        when(scimService.save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == verified &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )))).thenReturn(savedScimUser);

        // Act
        final UserActivationResponseDto response = sut.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);

        // Assert
        assertEquals(userActivationResponse, response);
        verify(userCreation, times(1)).setVerified(verified);
        verify(userCreation, times(1)).setUserId(userId);
        verify(userActivationResponse, times(1)).setVerified(verified);
        verify(userCreationRepository, times(2)).save(userCreation);
        verify(scimService, times(1)).save(argThat(matching(
                user -> user.getEmails().get(0).getValue().equals(email) &&
                        user.getUserName().equals(email) &&
                        user.isVerified() == true &&
                        user.getFamilyName().equals(lastName) &&
                        user.getGivenName().equals(firstName)
        )));
        verify(scimService, times(1)).addUserToGroups(userCreation);
        verify(emailSender, times(1)).sendEmailToConfirmVerification(
                eq(xForwardedProto), eq(xForwardedHost), eq(xForwardedPort),
                eq(email), argThat(matching(fn -> fn.contains(firstName) && fn.contains(lastName))));
    }
}