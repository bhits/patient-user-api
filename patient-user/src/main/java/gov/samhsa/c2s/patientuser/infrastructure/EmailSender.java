package gov.samhsa.c2s.patientuser.infrastructure;

public interface EmailSender {
    void sendEmailWithVerificationLink(String email, String emailToken, String recipientFullName);
    void sendEmailToConfirmVerification(String email, String recipientFullName);
    void sendEmailWithVerificationLinkAndLang(String email, String emailToken, String recipientFullName, String language);
}