package gov.samhsa.c2s.patientuser.service.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScopeAssignmentRequestDto {
    private final List<String> scopes;

    public ScopeAssignmentRequestDto() {
        scopes = new ArrayList<String>();
    }

    public ScopeAssignmentRequestDto(List<String> scopes) {
        this.scopes = scopes;
    }
}