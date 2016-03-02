package gov.samhsa.mhc.patientuser.service.exception;

public class EmailNotificationServiceException extends RuntimeException {
    public EmailNotificationServiceException() {
    }

    public EmailNotificationServiceException(String message) {
        super(message);
    }

    public EmailNotificationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotificationServiceException(Throwable cause) {
        super(cause);
    }

    public EmailNotificationServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
