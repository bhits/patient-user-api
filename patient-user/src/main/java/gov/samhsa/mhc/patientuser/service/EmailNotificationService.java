package gov.samhsa.mhc.patientuser.service;

import gov.samhsa.mhc.patientuser.service.dto.PatientDto;

/**
 * Created by burcak.ulug on 3/2/2016.
 */
public interface EmailNotificationService {
    void sendEmailWithVerificationLink(String emailToken, PatientDto patientDto);
}
