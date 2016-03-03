package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.service.dto.PatientDto;

public interface PhrService {
    PatientDto findPatientProfileById(Long patientId);
}
