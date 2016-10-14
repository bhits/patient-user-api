package gov.samhsa.c2s.patientuser.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserCreationResponseDto {
    @NotNull
    private Long id;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String firstName;

    @NotEmpty
    @Pattern(regexp = "^[\\w-]+(\\.[\\w-]+)*@([a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*?\\.[a-zA-Z]{2,6}|(\\d{1,3}\\.){3}\\d{1,3})(:\\d{4})?$")
    private String email;

    @Past
    private LocalDate birthDate;

    @NotEmpty
    private String genderCode;

    @NotEmpty
    private String verificationCode;

    private Instant emailTokenExpiration;

    private boolean verified;
}