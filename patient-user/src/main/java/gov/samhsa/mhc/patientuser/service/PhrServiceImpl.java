package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.common.oauth2.RestTemplateOAuth2TokenHelper;
import gov.samhsa.mhc.patientuser.service.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.exception.PhrPatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
        try {
            final PatientDto patientDto = RestTemplateOAuth2TokenHelper.getWithToken(restTemplate, oAuth2Authentication, url, PatientDto.class);
            return patientDto;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                e = new PhrPatientNotFoundException(e.getStatusCode(), e.getStatusText(), e.getResponseHeaders(), e.getResponseBodyAsByteArray(), null);
            }
            throw e;
        }
    }

    private final String toPatientProfileUri(Long patientId) {
        return phrApiBaseUri + "/patients/" + patientId + "/profile";
    }
}
