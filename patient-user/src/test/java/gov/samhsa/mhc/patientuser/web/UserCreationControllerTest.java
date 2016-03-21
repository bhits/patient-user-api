package gov.samhsa.mhc.patientuser.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.mhc.patientuser.infrastructure.exception.EmailSenderException;
import gov.samhsa.mhc.patientuser.infrastructure.exception.PhrPatientNotFoundException;
import gov.samhsa.mhc.patientuser.service.UserCreationService;
import gov.samhsa.mhc.patientuser.service.dto.UserActivationResponseDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationResponseDto;
import gov.samhsa.mhc.patientuser.service.exception.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

import static gov.samhsa.mhc.common.unit.matcher.ArgumentMatchers.matching;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserCreationControllerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ObjectMapper objectMapper;
    private MockMvc mvc;
    @Mock
    private UserCreationService userCreationService;
    @InjectMocks
    private UserCreationController sut;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(this.sut).build();
    }

    @Test
    public void testInitiateUserCreation() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patientId.intValue())))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.birthDate.[0]", is(year)))
                .andExpect(jsonPath("$.birthDate.[1]", is(month)))
                .andExpect(jsonPath("$.birthDate.[2]", is(day)))
                .andExpect(jsonPath("$.genderCode", is(genderCode)))
                .andExpect(jsonPath("$.verificationCode", is(verificationCode)))
                .andExpect(jsonPath("$.emailTokenExpiration", is(matching((BigDecimal bd) -> bd.longValue() == emailTokenExpiration.getEpochSecond()))));
    }

    @Test
    public void testInitiateUserCreation_Throws_UserIsAlreadyVerifiedException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenThrow(UserIsAlreadyVerifiedException.class);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void testInitiateUserCreation_Throws_PhrPatientNotFoundException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenThrow(PhrPatientNotFoundException.class);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testInitiateUserCreation_Throws_HttpClientErrorException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenThrow(HttpClientErrorException.class);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testInitiateUserCreation_Throws_UserActivationCannotBeVerifiedException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenThrow(UserActivationCannotBeVerifiedException.class);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void testInitiateUserCreation_Throws_EmailSenderException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenThrow(EmailSenderException.class);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testInitiateUserCreation_Throws_RuntimeException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final UserCreationRequestDto request = new UserCreationRequestDto();
        final UserCreationResponseDto response = new UserCreationResponseDto();
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        response.setEmailTokenExpiration(emailTokenExpiration);
        response.setEmail(email);
        response.setVerificationCode(verificationCode);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        request.setPatientId(patientId);
        when(userCreationService.initiateUserCreation(argThat(matching(
                req -> req.getPatientId().equals(patientId)
        )))).thenThrow(RuntimeException.class);

        // Act and Assert
        mvc.perform(post("/creations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetCurrentUserCreationInfo() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final UserCreationResponseDto userCreationResponse = new UserCreationResponseDto();
        userCreationResponse.setGenderCode(genderCode);
        userCreationResponse.setId(patientId);
        userCreationResponse.setLastName(lastName);
        userCreationResponse.setFirstName(firstName);
        userCreationResponse.setBirthDate(birthDate);
        userCreationResponse.setEmail(email);
        userCreationResponse.setEmailTokenExpiration(emailTokenExpiration);
        userCreationResponse.setVerificationCode(verificationCode);
        when(userCreationService.findUserCreationInfoByPatientId(patientId)).thenReturn(userCreationResponse);

        // Act and Assert
        mvc.perform(get("/creations?patientId=" + patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patientId.intValue())))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.birthDate.[0]", is(year)))
                .andExpect(jsonPath("$.birthDate.[1]", is(month)))
                .andExpect(jsonPath("$.birthDate.[2]", is(day)))
                .andExpect(jsonPath("$.genderCode", is(genderCode)))
                .andExpect(jsonPath("$.verificationCode", is(verificationCode)))
                .andExpect(jsonPath("$.emailTokenExpiration", is(matching((BigDecimal bd) -> bd.longValue() == emailTokenExpiration.getEpochSecond()))));
    }

    @Test
    public void testGetCurrentUserCreationInfo_Throws_PhrPatientNotFoundException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final UserCreationResponseDto userCreationResponse = new UserCreationResponseDto();
        userCreationResponse.setGenderCode(genderCode);
        userCreationResponse.setId(patientId);
        userCreationResponse.setLastName(lastName);
        userCreationResponse.setFirstName(firstName);
        userCreationResponse.setBirthDate(birthDate);
        userCreationResponse.setEmail(email);
        userCreationResponse.setEmailTokenExpiration(emailTokenExpiration);
        userCreationResponse.setVerificationCode(verificationCode);
        when(userCreationService.findUserCreationInfoByPatientId(patientId)).thenThrow(PhrPatientNotFoundException.class);

        // Act and Assert
        mvc.perform(get("/creations?patientId=" + patientId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrentUserCreationInfo_Throws_UserCreationNotFoundException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final UserCreationResponseDto userCreationResponse = new UserCreationResponseDto();
        userCreationResponse.setGenderCode(genderCode);
        userCreationResponse.setId(patientId);
        userCreationResponse.setLastName(lastName);
        userCreationResponse.setFirstName(firstName);
        userCreationResponse.setBirthDate(birthDate);
        userCreationResponse.setEmail(email);
        userCreationResponse.setEmailTokenExpiration(emailTokenExpiration);
        userCreationResponse.setVerificationCode(verificationCode);
        when(userCreationService.findUserCreationInfoByPatientId(patientId)).thenThrow(UserCreationNotFoundException.class);

        // Act and Assert
        mvc.perform(get("/creations?patientId=" + patientId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrentUserCreationInfo_Throws_RuntimeException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final Instant emailTokenExpiration = Instant.now();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final UserCreationResponseDto userCreationResponse = new UserCreationResponseDto();
        userCreationResponse.setGenderCode(genderCode);
        userCreationResponse.setId(patientId);
        userCreationResponse.setLastName(lastName);
        userCreationResponse.setFirstName(firstName);
        userCreationResponse.setBirthDate(birthDate);
        userCreationResponse.setEmail(email);
        userCreationResponse.setEmailTokenExpiration(emailTokenExpiration);
        userCreationResponse.setVerificationCode(verificationCode);
        when(userCreationService.findUserCreationInfoByPatientId(patientId)).thenThrow(RuntimeException.class);

        // Act and Assert
        mvc.perform(get("/creations?patientId=" + patientId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testActivateUser_Password_Length_Upper_Limit() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Paaaaaaaaaaaasword#1";
        final String confirmPassword = "Paaaaaaaaaaaasword#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patientId.intValue())))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.birthDate.[0]", is(year)))
                .andExpect(jsonPath("$.birthDate.[1]", is(month)))
                .andExpect(jsonPath("$.birthDate.[2]", is(day)))
                .andExpect(jsonPath("$.genderCode", is(genderCode)))
                .andExpect(jsonPath("$.verified", is(Boolean.TRUE)));
    }

    @Test
    public void testActivateUser_Password_Length_Lower_Limit() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Pwrd#1";
        final String confirmPassword = "Pwrd#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patientId.intValue())))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.birthDate.[0]", is(year)))
                .andExpect(jsonPath("$.birthDate.[1]", is(month)))
                .andExpect(jsonPath("$.birthDate.[2]", is(day)))
                .andExpect(jsonPath("$.genderCode", is(genderCode)))
                .andExpect(jsonPath("$.verified", is(Boolean.TRUE)));
    }

    @Test
    public void testActivateUser_Throws_PasswordConfirmationFailedException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Pwrd#1";
        final String confirmPassword = "Pwrd#1Different";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password) &&
                        req.getConfirmPassword().equals(confirmPassword)
        )))).thenThrow(PasswordConfirmationFailedException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void testActivateUser_Weak_Password_No_Upper_Case() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateUser_Weak_Password_No_Special_Character() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateUser_Weak_Password_No_Number() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateUser_Weak_Password_Too_Short() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Prd#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateUser_Weak_Password_Too_Long() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Paaaaaaaaaaaaasword#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenReturn(response);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateUser_Throws_UserActivationCannotBeVerifiedException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(UserActivationCannotBeVerifiedException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void testActivateUser_Throws_UserIsAlreadyVerifiedException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(UserIsAlreadyVerifiedException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void testActivateUser_Throws_EmailTokenExpiredException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(EmailTokenExpiredException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void testActivateUser_Throws_PhrPatientNotFoundException() throws Exception {
        // Arrange
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(PhrPatientNotFoundException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testActivateUser_Throws_HttpClientErrorException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(HttpClientErrorException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testActivateUser_Throws_EmailSenderException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(EmailSenderException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testActivateUser_Throws_RuntimeException() throws Exception {
        // Arrange
        thrown.expect(NestedServletException.class);
        final Long patientId = 10L;
        final UserActivationRequestDtoForTest request = new UserActivationRequestDtoForTest();
        final UserActivationResponseDto response = new UserActivationResponseDto();
        final String email = "email";
        final String verificationCode = "verificationCode";
        final int year = 2010;
        final int month = 2;
        final int day = 3;
        final LocalDate birthDate = LocalDate.of(year, month, day);
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String genderCode = "genderCode ";
        final String emailToken = "emailToken";
        final String password = "Password#1";
        final String confirmPassword = "Password#1";
        final boolean verified = true;
        response.setEmail(email);
        response.setBirthDate(birthDate);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setId(patientId);
        response.setGenderCode(genderCode);
        response.setBirthDate(birthDate);
        response.setGenderCode(genderCode);
        response.setVerified(verified);
        request.setVerificationCode(verificationCode);
        request.setBirthDate(Arrays.asList(year, month, day));
        request.setEmailToken(emailToken);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        when(userCreationService.activateUser(argThat(matching(
                req -> req.getBirthDate().equals(birthDate) &&
                        req.getVerificationCode().equals(verificationCode) &&
                        req.getEmailToken().equals(emailToken) &&
                        req.getPassword().equals(password)
        )))).thenThrow(RuntimeException.class);

        // Act and Assert
        mvc.perform(post("/activations")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isInternalServerError());
    }
}
