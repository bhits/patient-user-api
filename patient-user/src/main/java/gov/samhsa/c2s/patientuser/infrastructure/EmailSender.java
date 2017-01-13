package gov.samhsa.c2s.patientuser.infrastructure;

public interface EmailSender {
    void sendEmailWithVerificationLink(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String emailToken, String recipientFullName);

    void sendEmailToConfirmVerification(String xForwardedProto, String xForwardedHost, int xForwardedPort, String email, String recipientFullName);
}