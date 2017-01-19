package gov.samhsa.c2s.patientuser.web;

import gov.samhsa.c2s.patientuser.service.UserCreationService;
import gov.samhsa.c2s.patientuser.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@RestController
public class UserCreationController {

    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";

    @Autowired
    private UserCreationService userCreationService;

    @RequestMapping(value = "/creations", method = RequestMethod.POST)
    public UserCreationResponseDto initiateUserCreation(@Valid @RequestBody UserCreationRequestDto userCreationRequest,
                                                        @RequestHeader(X_FORWARDED_PROTO) String xForwardedProto,
                                                        @RequestHeader(X_FORWARDED_HOST) String xForwardedHost,
                                                        @RequestHeader(X_FORWARDED_PORT) int xForwardedPort) {
        final UserCreationResponseDto userCreationResponseDto = userCreationService.initiateUserCreation(userCreationRequest, xForwardedProto, xForwardedHost, xForwardedPort);
        return userCreationResponseDto;
    }

    @RequestMapping(value = "/creations", method = RequestMethod.GET)
    public UserCreationResponseDto getCurrentUserCreationInfo(@RequestParam("patientId") Long patientId) {
        return userCreationService.findUserCreationInfoByPatientId(patientId);
    }

    @RequestMapping(value = "/activations", method = RequestMethod.POST)
    public UserActivationResponseDto activateUser(@Valid @RequestBody UserActivationRequestDto userActivationRequest,
                                                  @RequestHeader(X_FORWARDED_PROTO) String xForwardedProto,
                                                  @RequestHeader(X_FORWARDED_HOST) String xForwardedHost,
                                                  @RequestHeader(X_FORWARDED_PORT) int xForwardedPort) {
        return userCreationService.activateUser(userActivationRequest, xForwardedProto, xForwardedHost, xForwardedPort);
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