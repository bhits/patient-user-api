package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.common.oauth2.RestTemplateOAuth2TokenHelper;
import gov.samhsa.mhc.patientuser.service.dto.PatientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PhrServiceImpl implements PhrService {

    @Value("${mhc.apis.phr}")
    private String phrApiBaseUri;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public PatientDto findPatientProfileById(OAuth2Authentication oAuth2Authentication, Long patientId) {
        final String url = toPatientProfileUri(patientId);
        final PatientDto patientDto = RestTemplateOAuth2TokenHelper.getWithToken(restTemplate, oAuth2Authentication, url, PatientDto.class);
        return patientDto;
    }

    private final String toPatientProfileUri(Long patientId){
        return phrApiBaseUri + "/patients/" + patientId + "/profile";
    }
}
