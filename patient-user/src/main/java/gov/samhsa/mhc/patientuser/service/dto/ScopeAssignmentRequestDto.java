package gov.samhsa.mhc.patientuser.service.dto;

import java.util.ArrayList;
import java.util.List;

public class ScopeAssignmentRequestDto {
    private final List<String> scopes;

    public ScopeAssignmentRequestDto() {
        scopes =  new ArrayList<String>();
    }
    public ScopeAssignmentRequestDto(List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getScopes() {
        return scopes;
    }
}
