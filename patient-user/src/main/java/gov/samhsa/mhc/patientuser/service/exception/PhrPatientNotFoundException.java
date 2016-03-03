package gov.samhsa.mhc.patientuser.service.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PhrPatientNotFoundException extends HttpClientErrorException {
    public PhrPatientNotFoundException(HttpStatus statusCode) {
        super(statusCode);
    }

    public PhrPatientNotFoundException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public PhrPatientNotFoundException(HttpStatus statusCode, String statusText, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseBody, responseCharset);
    }

    public PhrPatientNotFoundException(HttpStatus statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
