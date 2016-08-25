package gov.samhsa.c2s.patientuser.infrastructure;

import gov.samhsa.c2s.patientuser.domain.Scope;
import gov.samhsa.c2s.patientuser.domain.UserCreation;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;

public interface ScimService {
    ScimUser save(ScimUser scimUser);

    String findGroupIdByDisplayName(String groupDisplayName);

    String findUserIdByUserName(String username);

    ScimGroupMember addUserToGroup(UserCreation userCreation, Scope scope, String groupId);

    void addUserToGroups(UserCreation userCreation);

     void updateUserWithNewGroup(UserCreation userCreation, Scope scope);
}
