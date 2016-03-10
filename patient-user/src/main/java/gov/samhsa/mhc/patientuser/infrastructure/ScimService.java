package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.domain.Scope;
import gov.samhsa.mhc.patientuser.domain.UserCreation;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;

public interface ScimService {
    ScimUser save(ScimUser scimUser);

    String findGroupIdByDisplayName(String groupDisplayName);

    String findUserIdByUserName(String username);

    ScimGroupMember addUserToGroup(UserCreation userCreation, Scope scope, String groupId);

    void addUserToGroups(UserCreation userCreation);
}
