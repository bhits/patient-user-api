package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.domain.*;
import gov.samhsa.mhc.patientuser.infrastructure.EmailSender;
import gov.samhsa.mhc.patientuser.infrastructure.PhrService;
import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationResponseDto;
import gov.samhsa.mhc.patientuser.service.exception.UserCreationNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
        final UserType userType = userTypeRepository.findOneByType(UserTypeEnum.SELF);
        String emailToken = emailTokenGenerator.generateEmailToken();
        final Date emailTokenExpirationDate = Date.from(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant());
        final UserCreation userCreation = userCreationRepository.findOneByPatientIdAsOptional(patientDto.getId())
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
                patientDto.getFirstName() + " " + patientDto.getLastName());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserCreationResponseDto findUserCreationInfoByPatientId(Long patientId) {
        final PatientDto patientDto = phrService.findPatientProfileById(patientId);
        final UserCreation userCreation = userCreationRepository.findOneByPatientIdAsOptional(patientId).orElseThrow(() -> new UserCreationNotFoundException("No user creation record found for patient id: " + patientId));
        final UserCreationResponseDto response = modelMapper.map(patientDto, UserCreationResponseDto.class);
        response.setVerificationCode(userCreation.getVerificationCode());
        response.setEmailTokenExpiration(userCreation.getEmailTokenExpiration());
        return response;
    }
}
