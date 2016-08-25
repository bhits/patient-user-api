package gov.samhsa.c2s.patientuser.service.dto;

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

    public boolean isVerified() {
        return verified;
    }

    public String getUsername() {
        return username;
    }
}
