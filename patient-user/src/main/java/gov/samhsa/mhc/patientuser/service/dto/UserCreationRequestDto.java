package gov.samhsa.mhc.patientuser.service.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UserCreationRequestDto {
    @NotNull
    @Min(1)
    private Long patientId;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
}
