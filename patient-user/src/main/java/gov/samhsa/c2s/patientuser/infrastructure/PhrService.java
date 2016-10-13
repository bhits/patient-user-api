package gov.samhsa.c2s.patientuser.infrastructure;

import gov.samhsa.c2s.patientuser.infrastructure.dto.PatientDto;

public interface PhrService {
    PatientDto findPatientProfileById(Long patientId);

    PatientDto findPatientProfileById(Long patientId, boolean useClientCredentials);
}