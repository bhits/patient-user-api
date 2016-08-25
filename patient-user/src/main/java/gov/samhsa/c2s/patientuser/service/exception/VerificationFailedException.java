package gov.samhsa.c2s.patientuser.service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class VerificationFailedException extends RuntimeException {
    public VerificationFailedException() {
    }

    public VerificationFailedException(String message) {
        super(message);
    }

    public VerificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationFailedException(Throwable cause) {
        super(cause);
    }

    public VerificationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
