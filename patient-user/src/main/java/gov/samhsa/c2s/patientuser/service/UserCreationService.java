package gov.samhsa.c2s.patientuser.service;

import gov.samhsa.c2s.patientuser.service.dto.*;
import gov.samhsa.c2s.patientuser.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.patientuser.service.dto.ScopeAssignmentResponseDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface UserCreationService {
    @Transactional
    UserCreationResponseDto initiateUserCreation(UserCreationRequestDto userCreationRequest);

    @Transactional(readOnly = true)
    UserCreationResponseDto findUserCreationInfoByPatientId(Long patientId);

    @Transactional
    UserActivationResponseDto activateUser(UserActivationRequestDto userActivationRequest);

    @Transactional(readOnly = true)
    VerificationResponseDto verify(String emailToken, Optional<String> verificationCode, Optional<LocalDate> birthDate);

    ScopeAssignmentResponseDto assignScopeToUser(ScopeAssignmentRequestDto scopeAssignmentRequestDto);
}
