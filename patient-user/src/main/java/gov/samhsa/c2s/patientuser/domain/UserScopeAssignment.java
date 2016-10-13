package gov.samhsa.c2s.patientuser.domain;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_creation", "scope"}))
@Audited
public class UserScopeAssignment {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UserCreation userCreation;
    @ManyToOne
    private Scope scope;
    /**
     * Verify if scope is assign in UAA.
     */
    private boolean assigned = false;

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

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}