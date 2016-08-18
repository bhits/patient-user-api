package gov.samhsa.c2s.patientuser.infrastructure.exception;

public class EmailSenderException extends RuntimeException {
    public EmailSenderException() {
    }

    public EmailSenderException(String message) {
        super(message);
    }

    public EmailSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSenderException(Throwable cause) {
        super(cause);
    }

    public EmailSenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
