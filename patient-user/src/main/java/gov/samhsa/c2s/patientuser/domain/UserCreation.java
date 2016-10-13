package gov.samhsa.c2s.patientuser.domain;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "patientId"),
        indexes = @Index(columnList = "emailToken", name = "email_token_idx", unique = true))
@Audited
public class UserCreation {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UserType userType;
    @NotNull
    @Min(1)
    private Long patientId;
    @NotEmpty
    private String emailToken;
    @NotEmpty
    private String verificationCode;

    @NotNull
    @Future
    private Date emailTokenExpiration;

    @Transient
    private Instant emailTokenExpirationAsInstant;

    private boolean verified;

    private String userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

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

    public Date getEmailTokenExpiration() {
        return emailTokenExpiration;
    }

    public void setEmailTokenExpiration(Date emailTokenExpiration) {
        this.emailTokenExpirationAsInstant = (new Jsr310JpaConverters.InstantConverter()).convertToEntityAttribute(emailTokenExpiration);
        this.emailTokenExpiration = emailTokenExpiration;
    }

    public Instant getEmailTokenExpirationAsInstant() {
        if (Objects.nonNull(emailTokenExpiration)) {
            return (new Jsr310JpaConverters.InstantConverter()).convertToEntityAttribute(emailTokenExpiration);
        }
        return null;
    }

    public void setEmailTokenExpirationAsInstant(Instant emailTokenExpirationAsInstant) {
        this.emailTokenExpiration = (new Jsr310JpaConverters.InstantConverter()).convertToDatabaseColumn(emailTokenExpirationAsInstant);
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}