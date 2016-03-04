package gov.samhsa.mhc.patientuser.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class UserIsAlreadyVerifiedException extends RuntimeException {
    public UserIsAlreadyVerifiedException() {
    }

    public UserIsAlreadyVerifiedException(String message) {
        super(message);
    }

    public UserIsAlreadyVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIsAlreadyVerifiedException(Throwable cause) {
        super(cause);
    }

    public UserIsAlreadyVerifiedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
