package gov.samhsa.mhc.patientuser.domain;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_creation", "scope"}))
public class UserScopeAssignment {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UserCreation userCreation;
    @ManyToOne
    private Scope scope;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserCreation getUserCreation() {
        return userCreation;
    }

    public void setUserCreation(UserCreation userCreation) {
        this.userCreation = userCreation;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}