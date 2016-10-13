package gov.samhsa.c2s.patientuser.domain;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Audited
public class UserType {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private UserTypeEnum type;
    @OneToMany
    private List<Scope> scopes;
}