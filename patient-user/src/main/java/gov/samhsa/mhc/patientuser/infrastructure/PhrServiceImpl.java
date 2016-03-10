package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.config.ApplicationContextConfig;
import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.mhc.patientuser.infrastructure.exception.PhrPatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class PhrServiceImpl implements PhrService {

    @Value("${mhc.apis.phr}")
    private String phrApiBaseUri;

    @Autowired
    @Qualifier(ApplicationContextConfig.OAUTH2_REST_TEMPLATE)
    private OAuth2RestTemplate restTemplate;

    @Autowired
    @Qualifier(ApplicationContextConfig.OAUTH2_REST_TEMPLATE_CLIENT_CREDENTIALS)
    private OAuth2RestTemplate restTemplateWithClientCredentials;

    @Override
    public PatientDto findPatientProfileById(Long patientId) {
        return findPatientProfileById(patientId, false);
    }

    @Override
    public PatientDto findPatientProfileById(Long patientId, boolean useClientCredentials) {
        final String url = toPatientProfileUri(patientId);
        try {
            final RestTemplate selectedRestTemplate = useClientCredentials ? restTemplateWithClientCredentials : restTemplate;
            final PatientDto patientDto = selectedRestTemplate.getForObject(url, PatientDto.class);
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
