package gov.samhsa.mhc.patientuser.web;

import gov.samhsa.mhc.patientuser.service.UserCreationService;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationRequestDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserCreationController {

    @Autowired
    private UserCreationService userCreationService;

    @RequestMapping(value = "/userCreation", method = RequestMethod.POST)
    public UserCreationResponseDto initiateUserCreation(@Valid @RequestBody UserCreationRequestDto userCreationRequest) {
        final UserCreationResponseDto userCreationResponseDto = userCreationService.initiateUserCreation(userCreationRequest);
        return userCreationResponseDto;
    }
}
