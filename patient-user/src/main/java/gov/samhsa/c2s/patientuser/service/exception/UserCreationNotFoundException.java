package gov.samhsa.c2s.patientuser.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserCreationNotFoundException extends RuntimeException {
    public UserCreationNotFoundException() {
    }

    public UserCreationNotFoundException(String message) {
        super(message);
    }

    public UserCreationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCreationNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserCreationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
