package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.domain.*;
import gov.samhsa.mhc.patientuser.service.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.dto.UserCreationDto;
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

    @Override
    @Transactional
    public UserCreationDto initiateUserCreation(PatientDto patientDto) {
        final UserType userType = userTypeRepository.findOneByType(UserTypeEnum.PATIENT);
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
        final UserCreation saved = userCreationRepository.save(userCreation);
        final UserCreationDto response = modelMapper.map(saved, UserCreationDto.class);
        return response;
    }
}
