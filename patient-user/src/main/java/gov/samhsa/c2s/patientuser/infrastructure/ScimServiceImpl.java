package gov.samhsa.c2s.patientuser.infrastructure;

import gov.samhsa.c2s.patientuser.config.ApplicationContextConfig;
import gov.samhsa.c2s.patientuser.domain.Scope;
import gov.samhsa.c2s.patientuser.domain.UserCreation;
import gov.samhsa.c2s.patientuser.domain.UserScopeAssignment;
import gov.samhsa.c2s.patientuser.domain.UserScopeAssignmentRepository;
import gov.samhsa.c2s.patientuser.infrastructure.dto.IdentifierDto;
import gov.samhsa.c2s.patientuser.infrastructure.dto.SearchResultsWrapperWithId;
import gov.samhsa.c2s.patientuser.infrastructure.exception.IdCannotBeFoundException;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import java.util.Objects;

@Service
public class ScimServiceImpl implements ScimService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String scimBaseUrl;
    private final String usersEndpoint;
    private final String groupsEndpoint;

    @Autowired
    private UserScopeAssignmentRepository userScopeAssignmentRepository;

    @Autowired
    @Qualifier(ApplicationContextConfig.OAUTH2_REST_TEMPLATE_CLIENT_CREDENTIALS)
    private RestOperations restTemplate;

    @Autowired
    public ScimServiceImpl(@Value("${c2s.patient-user.scim.url}") String uaaBaseUrl) {
        Assert.hasText(uaaBaseUrl, "Missing SCIM endpoint");
        this.scimBaseUrl = uaaBaseUrl;
        this.usersEndpoint = this.scimBaseUrl + "/Users";
        this.groupsEndpoint = this.scimBaseUrl + "/Groups";
    }

    @Override
    public ScimUser save(ScimUser scimUser) {
        final ScimUser scimUserResp = restTemplate.postForObject(usersEndpoint, scimUser, ScimUser.class);
        return scimUserResp;
    }

    @Override
    public String findGroupIdByDisplayName(String groupDisplayName) {
        final SearchResultsWrapperWithId id = restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName + "\"&attributes=id", SearchResultsWrapperWithId.class);
        return extractId(id);
    }

    @Override
    public String findUserIdByUserName(String username) {
        final SearchResultsWrapperWithId id = restTemplate.getForObject(usersEndpoint + "?filter=userName eq \"" + username + "\"&attributes=id", SearchResultsWrapperWithId.class);
        return extractId(id);
    }

    @Override
    @Transactional
    public ScimGroupMember addUserToGroup(UserCreation userCreation, Scope scope, String groupId) {
        UserScopeAssignment userScopeAssignment = new UserScopeAssignment();
        ScimGroupMember scimGroupMemberResponse = null;
        try {
            userScopeAssignment.setUserCreation(userCreation);
            userScopeAssignment.setScope(userCreation.getUserType().getScopes().stream().filter(scope::equals).findAny().get());
            ScimGroupMember scimGroupMember = new ScimGroupMember(userCreation.getUserId());
            userScopeAssignment.setAssigned(true);
            userScopeAssignmentRepository.save(userScopeAssignment);
            scimGroupMemberResponse = restTemplate.postForObject(groupsEndpoint + "/{groupId}/members", scimGroupMember, ScimGroupMember.class, groupId);
        } catch (Exception e) {
            logger.error("Error in assigning scope to user in UAA.");
            userScopeAssignment.setAssigned(false);
            userScopeAssignmentRepository.save(userScopeAssignment);
        }

        return scimGroupMemberResponse;
    }

    @Override
    public void addUserToGroups(UserCreation userCreation) {
        userCreation.getUserType().getScopes().forEach(scope -> addUserToGroup(userCreation, scope, findGroupIdByDisplayName(scope.getScope())));
    }

    private static final String extractId(SearchResultsWrapperWithId searchResultsWrapperWithId) {
        return searchResultsWrapperWithId.getResources().stream()
                .filter(Objects::nonNull)
                .map(IdentifierDto::getId)
                .filter(StringUtils::hasText)
                .findAny().orElseThrow(IdCannotBeFoundException::new);
    }

    @Override
    public void updateUserWithNewGroup(UserCreation userCreation, Scope scope) {
        ScimGroupMember scimGroupMember = new ScimGroupMember(userCreation.getUserId());
        String groupId = findGroupIdByDisplayName(scope.getScope());
        final ScimGroupMember scimGroupMemberResponse = restTemplate.postForObject(groupsEndpoint + "/{groupId}/members", scimGroupMember, ScimGroupMember.class, groupId);
    }
}