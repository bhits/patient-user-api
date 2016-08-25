package gov.samhsa.c2s.patientuser.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class PasswordConfirmationFailedException extends RuntimeException {
    public PasswordConfirmationFailedException() {
    }

    public PasswordConfirmationFailedException(String message) {
        super(message);
    }

    public PasswordConfirmationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordConfirmationFailedException(Throwable cause) {
        super(cause);
    }

    public PasswordConfirmationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
