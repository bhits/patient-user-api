package gov.samhsa.c2s.patientuser.domain;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_creation_id", "scope_id"}))
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
}