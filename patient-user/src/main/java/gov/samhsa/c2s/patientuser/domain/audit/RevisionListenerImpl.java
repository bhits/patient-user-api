package gov.samhsa.c2s.patientuser.domain.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by tomson.ngassa on 3/22/2016.
 */
public class RevisionListenerImpl implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionInfoEntity revisionInfoEntity = (RevisionInfoEntity) revisionEntity;
        revisionInfoEntity.setUsername(getCurrentUserName());
    }

    private String getCurrentUserName(){
        String currentUserName = "ANONYMOUS";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        return currentUserName;
    }
}
