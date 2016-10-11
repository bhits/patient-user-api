package gov.samhsa.c2s.patientuser.service;

public interface TokenGenerator {
    String generateToken();

    String generateToken(int maxLength);
}
