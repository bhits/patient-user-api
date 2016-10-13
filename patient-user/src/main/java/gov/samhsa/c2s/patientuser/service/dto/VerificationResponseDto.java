package gov.samhsa.c2s.patientuser.service.dto;

import lombok.Data;

@Data
public class VerificationResponseDto {
    private final boolean verified;
    private final String username;

    public VerificationResponseDto(boolean verified) {
        this.verified = verified;
        this.username = null;
    }

    public VerificationResponseDto(boolean verified, String username) {
        this.verified = verified;
        this.username = username;
    }
}