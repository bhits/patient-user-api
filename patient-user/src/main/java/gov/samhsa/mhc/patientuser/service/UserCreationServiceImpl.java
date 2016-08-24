package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.common.log.Logger;
import gov.samhsa.mhc.common.log.LoggerFactory;
import gov.samhsa.mhc.patientuser.domain.*;
import gov.samhsa.mhc.patientuser.infrastructure.EmailSender;
import gov.samhsa.mhc.patientuser.infrastructure.PhrService;
import gov.samhsa.mhc.patientuser.infrastructure.ScimService;
import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.dto.*;
import gov.samhsa.mhc.patientuser.service.exception.*;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserCreationServiceImpl implements UserCreationService {

    private Logger logger = LoggerFactory.getLogger(this);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private UserCreationRepository userCreationRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private EmailTokenGenerator emailTokenGenerator;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PhrService phrService;

    @Autowired
    private ScimService scimService;

    @Autowired
    private ScopeRepository scopeRepository;

    @Value("${mhc.patient-user.config.email-token-expiration-in-days}")
    private int emailTokenExpirationInDays;

    @Autowired
    private UserScopeAssignmentRepository userScopeAssignmentRepository;

    @Override
    @Transactional
    public UserCreationResponseDto initiateUserCreation(UserCreationRequestDto userCreationRequest) {
        // Find patient on PHR
        final PatientDto patientDto = phrService.findPatientProfileById(userCreationRequest.getPatientId());
        // Create/Update record for patient user creation
        final UserType userType = userTypeRepository.findOneByType(UserTypeEnum.SELF).get();
        String emailToken = emailTokenGenerator.generateEmailToken();
        final Instant emailTokenExpirationDate = Instant.now().plus(Period.ofDays(emailTokenExpirationInDays));
        final UserCreation userCreation = userCreationRepository.findOneByPatientId(patientDto.getId())
                .orElseGet(UserCreation::new);
        assertNotAlreadyVerified(userCreation);
        userCreation.setEmailTokenExpirationAsInstant(emailTokenExpirationDate);
        userCreation.setEmailToken(emailToken);
        userCreation.setPatientId(patientDto.getId());
        userCreation.setUserType(userType);
        userCreation.setVerified(false);
        userCreation.setVerificationCode(tokenGenerator.generateToken(7));
        // Persists record
        final UserCreation saved = userCreationRepository.save(userCreation);
        // Prepare response for the patient user creation
        final UserCreationResponseDto response = modelMapper.map(patientDto, UserCreationResponseDto.class);
        response.setBirthDate(patientDto.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        response.setVerificationCode(saved.getVerificationCode());
        response.setEmailTokenExpiration(saved.getEmailTokenExpirationAsInstant());
        response.setVerified(saved.isVerified());
        // Send email with verification link
        emailSender.sendEmailWithVerificationLink(
                patientDto.getEmail(),
                saved.getEmailToken(),
                getRecipientFullName(patientDto));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserCreationResponseDto findUserCreationInfoByPatientId(Long patientId) {
        final PatientDto patientDto = phrService.findPatientProfileById(patientId);
        final UserCreation userCreation = userCreationRepository.findOneByPatientId(patientId).orElseThrow(() -> new UserCreationNotFoundException("No user creation record found for patient id: " + patientId));
        final UserCreationResponseDto response = modelMapper.map(patientDto, UserCreationResponseDto.class);
        response.setBirthDate(patientDto.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        response.setVerificationCode(userCreation.getVerificationCode());
        response.setEmailTokenExpiration(userCreation.getEmailTokenExpirationAsInstant());
        response.setVerified(userCreation.isVerified());
        return response;
    }

    @Override
    public UserActivationResponseDto activateUser(UserActivationRequestDto userActivationRequest) {
        // Verify password
        assertPasswordAndConfirmPassword(userActivationRequest);
        // Find user creation process with emailToken and verificationCode
        final UserCreation userCreation = userCreationRepository.findOneByEmailTokenAndVerificationCode(
                userActivationRequest.getEmailToken(),
                userActivationRequest.getVerificationCode())
                .orElseThrow(UserActivationCannotBeVerifiedException::new);

        // Assert user creation process preconditions
        assertNotAlreadyVerified(userCreation);
        assertEmailTokenNotExpired(userCreation);
        // Find patient profile on PHR
        final PatientDto patientProfile = phrService.findPatientProfileById(userCreation.getPatientId(), true);
        // Assert username and patient email match
        assertUsernameAndPatientEmailMatch(userActivationRequest, patientProfile);
        // Assert birth date verification
        assertBirthDateVerification(userActivationRequest, patientProfile);
        userCreation.setVerified(true);
        userCreationRepository.save(userCreation);
        // Prepare response
        final UserActivationResponseDto response = modelMapper.map(patientProfile, UserActivationResponseDto.class);
        response.setBirthDate(patientProfile.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        response.setVerified(userCreation.isVerified());
        // Create user using SCIM
        ScimUser scimUser = new ScimUser(null, patientProfile.getEmail(), patientProfile.getFirstName(), patientProfile.getLastName());
        scimUser.setPassword(userActivationRequest.getPassword());
        ScimUser.Email email = new ScimUser.Email();
        email.setValue(patientProfile.getEmail());
        scimUser.setEmails(Collections.singletonList(email));
        scimUser.setVerified(true);
        // Save SCIM user
        final ScimUser savedScimUser = scimService.save(scimUser);
        final String userId = savedScimUser.getId();
        Assert.hasText(userId, "SCIM userId must have text");
        // Save userId in userCreation
        userCreation.setUserId(userId);
        userCreationRepository.save(userCreation);
        // Add user to groups
        scimService.addUserToGroups(userCreation);
        emailSender.sendEmailToConfirmVerification(
                patientProfile.getEmail(),
                getRecipientFullName(patientProfile));
        return response;

    }

    @Override
    @Transactional(readOnly = true)
    public VerificationResponseDto verify(String emailToken, Optional<String> verificationCode, Optional<LocalDate> birthDate) {
        try {
            Assert.hasText(emailToken, "emailToken must have text");
            final Instant now = Instant.now();
            // Only emailToken is available
            if (!verificationCode.isPresent() && !birthDate.isPresent()) {
                final Optional<UserCreation> userCreationOptional = userCreationRepository.findOneByEmailToken(emailToken);
                if (userCreationOptional.filter(uc -> uc.isVerified() == true).isPresent()) {
                    throw new UserIsAlreadyVerifiedException();
                }
                final Boolean verified = userCreationRepository.findOneByEmailToken(emailToken)
                        .map(UserCreation::getEmailTokenExpirationAsInstant)
                        .map(expiration -> expiration.isAfter(now))
                        .filter(Boolean.TRUE::equals)
                        .orElseThrow(VerificationFailedException::new);
                return new VerificationResponseDto(verified);
            } else {
                // All arguments must be available
                final String verificationCodeNullSafe = verificationCode.filter(StringUtils::hasText).orElseThrow(VerificationFailedException::new);
                final LocalDate birthDateNullSafe = birthDate.filter(Objects::nonNull).orElseThrow(VerificationFailedException::new);
                // Assert user creation email token
                assertEmailTokenNotExpired(userCreationRepository.findOneByEmailToken(emailToken).get());
                final Long patientId = userCreationRepository
                        .findOneByEmailTokenAndVerificationCode(emailToken, verificationCodeNullSafe)
                        .filter(uc -> uc.getEmailTokenExpirationAsInstant().isAfter(now))
                        .map(UserCreation::getPatientId)
                        .orElseThrow(VerificationFailedException::new);
                final PatientDto patientDto = phrService.findPatientProfileById(patientId, true);
                final boolean verified = Optional.of(patientDto)
                        .map(PatientDto::getBirthDate)
                        .map(Date::toInstant)
                        .map(i -> i.atZone(ZoneId.systemDefault()))
                        .map(ZonedDateTime::toLocalDate)
                        .map(birthDateNullSafe::equals)
                        .filter(Boolean.TRUE::equals)
                        .orElseThrow(VerificationFailedException::new);
                final String username = Optional.of(patientDto)
                        .map(PatientDto::getEmail)
                        .orElseThrow(VerificationFailedException::new);
                return new VerificationResponseDto(verified, username);
            }
        } catch (EmailTokenExpiredException e) {
            logger.info(() -> "EmailToken expired: " + e.getMessage());
            logger.debug(() -> e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.info(() -> "Verification failed: " + e.getMessage());
            logger.debug(() -> e.getMessage(), e);
            throw new VerificationFailedException();
        }
    }

    private String getRecipientFullName(PatientDto patientProfile) {
        return patientProfile.getFirstName() + " " + patientProfile.getLastName();
    }

    private void assertUsernameAndPatientEmailMatch(UserActivationRequestDto userActivationRequest, PatientDto patientProfile) {
        final String usernameInRequest = Optional.of(userActivationRequest).map(UserActivationRequestDto::getUsername).filter(StringUtils::hasText).orElseThrow(UserActivationCannotBeVerifiedException::new);
        final String emailInPhr = Optional.of(patientProfile).map(PatientDto::getEmail).filter(StringUtils::hasText).orElseThrow(UserActivationCannotBeVerifiedException::new);
        if (!emailInPhr.equals(usernameInRequest)) {
            throw new UserActivationCannotBeVerifiedException();
        }
    }

    private void assertBirthDateVerification(UserActivationRequestDto userActivationRequest, PatientDto patientProfile) {
        final LocalDate birthDayInRequest = userActivationRequest.getBirthDate();
        final LocalDate birthDayInPhr = LocalDate.from(patientProfile.getBirthDate().toInstant().atZone(ZoneId.systemDefault()));
        if (!birthDayInPhr.equals(birthDayInRequest)) {
            throw new UserActivationCannotBeVerifiedException();
        }
    }

    private void assertNotAlreadyVerified(UserCreation userCreation) {
        if (userCreation.isVerified()) {
            throw new UserIsAlreadyVerifiedException();
        }
    }

    private void assertEmailTokenNotExpired(UserCreation userCreation) {
        if (userCreation.getEmailTokenExpirationAsInstant().isBefore(Instant.now())) {
            throw new EmailTokenExpiredException();
        }
    }

    private void assertPasswordAndConfirmPassword(UserActivationRequestDto userActivationRequest) {
        if (!userActivationRequest.getPassword().equals(userActivationRequest.getConfirmPassword())) {
            throw new PasswordConfirmationFailedException();
        }
    }

    public ScopeAssignmentResponseDto assignScopeToUser(ScopeAssignmentRequestDto scopeAssignmentRequestDto){
            scopeAssignmentRequestDto.getScopes().stream()
                .forEach(scope -> {
                    Scope foundScope = Optional.ofNullable(scopeRepository.findByScope(scope)).orElseThrow(ScopeDoesNotExistInDBException::new);
                    assignNewScopesToUsers(foundScope);
                });
        return null;
    }

    private void assignNewScopesToUsers(Scope scope){
        userCreationRepository.findAll().stream()
                .forEach(userCreation -> {
                    UserScopeAssignment userScopeAssignment =  new UserScopeAssignment();
                    userScopeAssignment.setScope(scope);
                    userScopeAssignment.setUserCreation(userCreation);
                    try {
                        userScopeAssignment.setAssigned(true);
                        userScopeAssignmentRepository.save(userScopeAssignment);
                        scimService.updateUserWithNewGroup(userCreation, scope);
                    }catch(Exception e){
                        logger.error("Error in assigning scope ot user in UAA.");
                        userScopeAssignment.setAssigned(false);
                        userScopeAssignmentRepository.save(userScopeAssignment);
                    }
                });
    }
}
