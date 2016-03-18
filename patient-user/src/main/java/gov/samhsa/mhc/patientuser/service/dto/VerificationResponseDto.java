package gov.samhsa.mhc.patientuser.service.dto;

public class VerificationResponseDto {
    private final boolean verified;

    public VerificationResponseDto(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }
}
