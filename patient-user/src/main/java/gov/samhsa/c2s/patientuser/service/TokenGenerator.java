package gov.samhsa.c2s.patientuser.service;

/**
 * Created by burcak.ulug on 3/2/2016.
 */
public interface TokenGenerator {
    String generateToken();

    String generateToken(int maxLength);
}