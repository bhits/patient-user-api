package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.service.dto.PatientDto;
import gov.samhsa.mhc.patientuser.service.exception.PhrPatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class PhrServiceImpl implements PhrService {

    @Value("${mhc.apis.phr}")
    private String phrApiBaseUri;

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Override
    public PatientDto findPatientProfileById(Long patientId) {
        final String url = toPatientProfileUri(patientId);
        try {
            final PatientDto patientDto = restTemplate.getForObject(url, PatientDto.class);
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
