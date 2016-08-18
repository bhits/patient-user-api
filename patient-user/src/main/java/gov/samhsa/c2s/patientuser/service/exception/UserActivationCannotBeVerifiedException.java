package gov.samhsa.c2s.patientuser.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class UserActivationCannotBeVerifiedException extends RuntimeException {
    public UserActivationCannotBeVerifiedException() {
    }

    public UserActivationCannotBeVerifiedException(String message) {
        super(message);
    }

    public UserActivationCannotBeVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserActivationCannotBeVerifiedException(Throwable cause) {
        super(cause);
    }

    public UserActivationCannotBeVerifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
