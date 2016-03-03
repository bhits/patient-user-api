package gov.samhsa.mhc.patientuser.infrastructure;

public interface EmailSender {
    void sendEmailWithVerificationLink(String email, String emailToken, String recipientFullName);
}
