package gov.samhsa.c2s.patientuser.service.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

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

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
