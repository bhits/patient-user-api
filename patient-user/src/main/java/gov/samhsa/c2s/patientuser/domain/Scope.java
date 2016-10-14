package gov.samhsa.c2s.patientuser.domain;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"scope"}))
@Audited
@Data
public class Scope {
    @Id
    @GeneratedValue
    private Long id;
    private String scope;
}