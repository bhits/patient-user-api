package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.domain.*;
import gov.samhsa.mhc.patientuser.infrastructure.EmailSender;
import gov.samhsa.mhc.patientuser.infrastructure.PhrService;
import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.dto.UserActivationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserActivationResponseDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationResponseDto;
import gov.samhsa.mhc.patientuser.service.exception.EmailTokenExpiredException;
import gov.samhsa.mhc.patientuser.service.exception.UserActivationCannotBeVerifiedException;
import gov.samhsa.mhc.patientuser.service.exception.UserCreationNotFoundException;
import gov.samhsa.mhc.patientuser.service.exception.UserIsAlreadyVerifiedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

@Service
public class UserCreationServiceImpl implements UserCreationService {

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

    @Override
    @Transactional
    public UserCreationResponseDto initiateUserCreation(UserCreationRequestDto userCreationRequest) {
        // Find patient on PHR
        final PatientDto patientDto = phrService.findPatientProfileById(userCreationRequest.getPatientId());
        // Create/Update record for patient user creation
        final UserType userType = userTypeRepository.findOneByType(UserTypeEnum.SELF).get();
        String emailToken = emailTokenGenerator.generateEmailToken();
        final Instant emailTokenExpirationDate = Instant.now().plus(Period.ofDays(7));
        final UserCreation userCreation = userCreationRepository.findOneByPatientId(patientDto.getId())
                .orElseGet(UserCreation::new);
        userCreation.setEmailTokenExpiration(emailTokenExpirationDate);
        userCreation.setEmailToken(emailToken);
        userCreation.setPatientId(patientDto.getId());
        userCreation.setUserType(userType);
        userCreation.setVerified(false);
        userCreation.setVerificationCode(tokenGenerator.generateToken(7));
        // Persists record
        final UserCreation saved = userCreationRepository.save(userCreation);
        // Prepare response for the patient user creation
        final UserCreationResponseDto response = modelMapper.map(patientDto, UserCreationResponseDto.class);
        response.setVerificationCode(saved.getVerificationCode());
        response.setEmailTokenExpiration(saved.getEmailTokenExpiration());
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
        response.setVerificationCode(userCreation.getVerificationCode());
        response.setEmailTokenExpiration(userCreation.getEmailTokenExpiration());
        return response;
    }

    @Override
    public UserActivationResponseDto activateUser(UserActivationRequestDto userActivationRequest) {
        // Find user creation process with emailToken and verificationCode
        final UserCreation userCreation = userCreationRepository.findOneByEmailTokenAndVerificationCode(
                userActivationRequest.getEmailToken(),
                userActivationRequest.getVerificationCode())
                .orElseThrow(UserActivationCannotBeVerifiedException::new);
        // Assert user creation process preconditions
        assertNotAlreadyVerified(userCreation);
        assertEmailTokenNotExpired(userCreation);
        // Find patient profile on PHR
        final PatientDto patientProfile = phrService.findPatientProfileById(userCreation.getPatientId());
        // Assert birth date verification
        assertBirthDateVerification(userActivationRequest, patientProfile);
        userCreation.setVerified(true);
        userCreationRepository.save(userCreation);
        // Prepare response
        final UserActivationResponseDto response = modelMapper.map(patientProfile, UserActivationResponseDto.class);
        response.setVerified(userCreation.isVerified());
        emailSender.sendEmailToConfirmVerification(
                patientProfile.getEmail(),
                getRecipientFullName(patientProfile));
        return response;
    }

    private String getRecipientFullName(PatientDto patientProfile) {
        return patientProfile.getFirstName() + " " + patientProfile.getLastName();
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
        if (userCreation.getEmailTokenExpiration().isBefore(Instant.now())) {
            throw new EmailTokenExpiredException();
        }
    }
}
