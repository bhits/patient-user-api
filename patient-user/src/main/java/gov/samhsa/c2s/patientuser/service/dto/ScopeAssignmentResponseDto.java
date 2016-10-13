package gov.samhsa.c2s.patientuser.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScopeAssignmentResponseDto {
    private final boolean assiged;
}