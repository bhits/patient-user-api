package gov.samhsa.mhc.patientuser.infrastructure;

import feign.FeignException;
import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.mhc.patientuser.infrastructure.exception.PhrPatientNotFoundException;
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
            final PatientDto patientDto = selectedClient.apply(patientId);
            return patientDto;
        } catch (FeignException e) {
            if (e.status() == (HttpStatus.NOT_FOUND.value())) {
                throw new PhrPatientNotFoundException(HttpStatus.NOT_FOUND, e.getMessage());
            }
            throw e;
        }
    }
}
