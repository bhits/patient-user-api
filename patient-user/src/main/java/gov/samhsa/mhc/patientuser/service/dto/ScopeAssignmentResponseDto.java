package gov.samhsa.mhc.patientuser.service.dto;

public class ScopeAssignmentResponseDto {
    private final boolean assiged;

    public ScopeAssignmentResponseDto(boolean assiged) {
        this.assiged = assiged;
    }

    public boolean isAssiged() {
        return assiged;
    }
}
