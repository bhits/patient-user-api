package gov.samhsa.c2s.patientuser.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ScopeDoesNotExistInDBException extends RuntimeException {
    public ScopeDoesNotExistInDBException() {
    }

    public ScopeDoesNotExistInDBException(String message) {
        super(message);
    }

    public ScopeDoesNotExistInDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScopeDoesNotExistInDBException(Throwable cause) {
        super(cause);
    }

    public ScopeDoesNotExistInDBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
