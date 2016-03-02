package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.service.dto.PatientDto;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * Created by burcak.ulug on 3/2/2016.
 */
public interface PhrService {
    PatientDto findPatientProfileById(OAuth2Authentication oAuth2Authentication, Long patientId);
}
