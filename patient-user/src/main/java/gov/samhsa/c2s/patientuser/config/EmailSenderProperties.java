package gov.samhsa.c2s.patientuser.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "c2s.patient-user.email-sender")
@Data
public class EmailSenderProperties {

    @NotEmpty
    private String ppUiRoute;

    @NotEmpty
    private String ppUiVerificationRelativePath;

    @NotEmpty
    private String ppUiVerificationEmailTokenArgName;

    @NotEmpty
    private String brand;

    @NotNull
    @Min(0)
    private int emailTokenExpirationInDays;
}