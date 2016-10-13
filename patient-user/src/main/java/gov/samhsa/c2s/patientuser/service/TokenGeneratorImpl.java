package gov.samhsa.c2s.patientuser.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class TokenGeneratorImpl implements TokenGenerator {
    @Override
    public String generateToken() {
        SecureRandom random = new SecureRandom();
        final String token = new BigInteger(130, random).toString(32);
        return token;
    }

    @Override
    public String generateToken(int maxLength) {
        final String token = generateToken();
        return token.substring(0, Integer.min(token.length(), maxLength));
    }
}