package gov.samhsa.c2s.patientuser.domain.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RevisionListenerImpl implements RevisionListener {

    private static final String DEFAULT_CURRENT_USER_NAME = "ANONYMOUS";

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionInfoEntity revisionInfoEntity = (RevisionInfoEntity) revisionEntity;
        revisionInfoEntity.setUsername(getCurrentUserName());
    }

    private String getCurrentUserName(){
        String currentUserName = DEFAULT_CURRENT_USER_NAME;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }

        return currentUserName;
    }
}