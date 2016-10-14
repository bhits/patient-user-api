package gov.samhsa.c2s.patientuser.service.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserCreationRequestDto {
    @NotNull
    @Min(1)
    private Long patientId;
}