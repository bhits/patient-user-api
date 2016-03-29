package gov.samhsa.mhc.patientuser.domain.audit;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by tomson.ngassa on 3/22/2016.
 */
@Entity
@Table(name = "REVINFO")
@RevisionEntity(RevisionListenerImpl.class)
public class RevisionInfoEntity extends DefaultRevisionEntity {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
