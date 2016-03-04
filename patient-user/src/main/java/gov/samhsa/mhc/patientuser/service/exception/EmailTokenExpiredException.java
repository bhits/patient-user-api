package gov.samhsa.mhc.patientuser.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class EmailTokenExpiredException extends RuntimeException {
    public EmailTokenExpiredException() {
    }

    public EmailTokenExpiredException(String message) {
        super(message);
    }

    public EmailTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailTokenExpiredException(Throwable cause) {
        super(cause);
    }

    public EmailTokenExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
