package gov.samhsa.mhc.patientuser.infrastructure;

public interface EmailSender {
    void sendEmailWithVerificationLink(String email, String emailToken, String recipientFullName);
    void sendEmailToConfirmVerification(String email, String recipientFullName);
}
