package gov.samhsa.mhc.patientuser.web;

import gov.samhsa.mhc.patientuser.service.UserCreationService;
import gov.samhsa.mhc.patientuser.service.dto.*;
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
}
