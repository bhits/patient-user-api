package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;

public interface PhrService {
    PatientDto findPatientProfileById(Long patientId);

    PatientDto findPatientProfileById(Long patientId, boolean useClientCredentials);
}
