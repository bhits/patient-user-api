package gov.samhsa.c2s.patientuser.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class UserActivationRequestDto {

    @NotEmpty
    private String emailToken;
    @NotEmpty
    private String verificationCode;

    @Past
    private LocalDate birthDate;

    @NotEmpty
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$% !\"&'()*+,-./:;<=>?\\\\\\[\\]^_`{|}~]).{6,20})")
    private String password;

    @NotEmpty
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$% !\"&'()*+,-./:;<=>?\\\\\\[\\]^_`{|}~]).{6,20})")
    private String confirmPassword;

    @NotEmpty
    private String username;
}
