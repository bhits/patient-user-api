package gov.samhsa.c2s.patientuser.infrastructure;

import feign.FeignException;
import gov.samhsa.c2s.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.c2s.patientuser.infrastructure.exception.PhrPatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.function.Function;

@Service
public class PhrServiceImpl implements PhrService {

    @Autowired
    private PhrServiceDefault phrServiceDefault;

    @Autowired
    private PhrServiceClientCredentials phrServiceClientCredentials;

    @Override
    public PatientDto findPatientProfileById(Long patientId) {
        return findPatientProfileById(patientId, false);
    }

    @Override
    public PatientDto findPatientProfileById(Long patientId, boolean useClientCredentials) {
        Assert.notNull(patientId, "patientId cannot be null");
        try {
            final Function<Long, PatientDto> selectedClient = useClientCredentials ? phrServiceClientCredentials::findPatientProfileById : phrServiceDefault::findPatientProfileById;
            return selectedClient.apply(patientId);
        } catch (FeignException e) {
            if (e.status() == (HttpStatus.NOT_FOUND.value())) {
                throw new PhrPatientNotFoundException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw e;
        }
    }
}