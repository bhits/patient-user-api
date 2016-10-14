package gov.samhsa.c2s.patientuser.infrastructure.exception;

public class IdCannotBeFoundException extends RuntimeException {
    public IdCannotBeFoundException() {
    }

    public IdCannotBeFoundException(String message) {
        super(message);
    }

    public IdCannotBeFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdCannotBeFoundException(Throwable cause) {
        super(cause);
    }

    public IdCannotBeFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}