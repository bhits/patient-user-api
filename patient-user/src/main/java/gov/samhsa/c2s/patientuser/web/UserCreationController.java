package gov.samhsa.c2s.patientuser.web;

import gov.samhsa.c2s.patientuser.service.UserCreationService;
import gov.samhsa.c2s.patientuser.service.dto.*;
import gov.samhsa.c2s.patientuser.service.dto.ScopeAssignmentRequestDto;
import gov.samhsa.c2s.patientuser.service.dto.ScopeAssignmentResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@RestController
public class UserCreationController {

    @Autowired
    private UserCreationService userCreationService;

    @RequestMapping(value = "/creations", method = RequestMethod.POST)
    public UserCreationResponseDto initiateUserCreation(@Valid @RequestBody UserCreationRequestDto userCreationRequest) {
        final UserCreationResponseDto userCreationResponseDto = userCreationService.initiateUserCreation(userCreationRequest);
        return userCreationResponseDto;
    }

    @RequestMapping(value = "/creations", method = RequestMethod.GET)
    public UserCreationResponseDto getCurrentUserCreationInfo(@RequestParam("patientId") Long patientId) {
        return userCreationService.findUserCreationInfoByPatientId(patientId);
    }

    @RequestMapping(value = "/activations", method = RequestMethod.POST)
    public UserActivationResponseDto activateUser(@Valid @RequestBody UserActivationRequestDto userActivationRequest) {
        return userCreationService.activateUser(userActivationRequest);
    }

    @RequestMapping(value = "/verifications", method = RequestMethod.GET)
    public VerificationResponseDto verify(@RequestParam("emailToken") String emailToken,
                                          @RequestParam("verificationCode") Optional<String> verificationCode,
                                          @RequestParam("birthDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> birthDate) {
        return userCreationService.verify(emailToken, verificationCode, birthDate);
    }

    @RequestMapping(value = "/scopeAssignments", method = RequestMethod.POST)
    public ScopeAssignmentResponseDto assignScope(@Valid @RequestBody ScopeAssignmentRequestDto scopeAssignmentRequestDto) {
        return userCreationService.assignScopeToUser(scopeAssignmentRequestDto);
    }
}