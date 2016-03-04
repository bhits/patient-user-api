package gov.samhsa.mhc.patientuser.web;

import gov.samhsa.mhc.patientuser.service.UserCreationService;
import gov.samhsa.mhc.patientuser.service.dto.UserActivationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserActivationResponseDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserCreationController {

    @Autowired
    private UserCreationService userCreationService;

    @RequestMapping(value = "/userCreations", method = RequestMethod.POST)
    public UserCreationResponseDto initiateUserCreation(@Valid @RequestBody UserCreationRequestDto userCreationRequest) {
        final UserCreationResponseDto userCreationResponseDto = userCreationService.initiateUserCreation(userCreationRequest);
        return userCreationResponseDto;
    }

    @RequestMapping(value = "/userCreations", method = RequestMethod.GET)
    public UserCreationResponseDto getCurrentUserCreationInfo(@RequestParam("patientId") Long patientId){
        return userCreationService.findUserCreationInfoByPatientId(patientId);
    }

    @RequestMapping(value = "/userActivations", method = RequestMethod.POST)
    public UserActivationResponseDto activateUser(@Valid @RequestBody UserActivationRequestDto userActivationRequest){
        return userCreationService.activateUser(userActivationRequest);
    }
}
