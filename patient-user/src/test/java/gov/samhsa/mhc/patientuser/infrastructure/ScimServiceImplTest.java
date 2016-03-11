package gov.samhsa.mhc.patientuser.infrastructure;

import gov.samhsa.mhc.patientuser.domain.*;
import gov.samhsa.mhc.patientuser.infrastructure.dto.IdentifierDto;
import gov.samhsa.mhc.patientuser.infrastructure.dto.SearchResultsWrapperWithId;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static gov.samhsa.mhc.common.unit.matcher.ArgumentMatchers.matching;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScimServiceImplTest {

    private final String scimBaseUrl = "scimBaseUrl";
    private final String usersEndpoint = scimBaseUrl + "/Users";
    private final String groupsEndpoint = scimBaseUrl + "/Groups";
    @Mock
    private UserScopeAssignmentRepository userScopeAssignmentRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ScimServiceImpl sut = new ScimServiceImpl(scimBaseUrl);

    @Test
    public void testSave() throws Exception {
        // Arrange
        final String id = "id";
        final ScimUser scimUser = new ScimUser();
        final ScimUser scimUserResponse = new ScimUser();
        scimUserResponse.setId(id);
        when(restTemplate.postForObject(usersEndpoint, scimUser, ScimUser.class)).thenReturn(scimUserResponse);

        // Act
        final ScimUser response = sut.save(scimUser);

        // Assert
        assertEquals(scimUserResponse, response);
    }

    @Test
    public void testFindGroupIdByDisplayName() throws Exception {
        // Arrange
        final String id = "id";
        final String groupDisplayName = "groupDisplayName";
        final SearchResultsWrapperWithId searchResultsMock = mock(SearchResultsWrapperWithId.class);
        final IdentifierDto identifier = new IdentifierDto();
        identifier.setId(id);
        when(searchResultsMock.getResources()).thenReturn(Arrays.asList(identifier));
        when(restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock);

        // Act
        final String response = sut.findGroupIdByDisplayName(groupDisplayName);

        // Assert
        assertEquals(id, response);
    }

    @Test
    public void testFindUserIdByUserName() throws Exception {
        // Arrange
        final String id = "id";
        final String username = "username";
        final SearchResultsWrapperWithId searchResultsMock = mock(SearchResultsWrapperWithId.class);
        final IdentifierDto identifier = new IdentifierDto();
        identifier.setId(id);
        when(searchResultsMock.getResources()).thenReturn(Arrays.asList(identifier));
        when(restTemplate.getForObject(usersEndpoint + "?filter=userName eq \"" + username + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock);

        // Act
        final String response = sut.findUserIdByUserName(username);

        // Assert
        assertEquals(id, response);
    }

    @Test
    public void testAddUserToGroup_Scope1() throws Exception {
        // Arrange
        final String userId = "userId";
        final String groupId = "groupId";
        final UserCreation userCreation = mock(UserCreation.class);
        final UserType userType = mock(UserType.class);
        final Scope scope1 = mock(Scope.class);
        final Scope scope2 = mock(Scope.class);
        when(userType.getScopes()).thenReturn(Arrays.asList(scope1, scope2));
        when(userCreation.getUserType()).thenReturn(userType);
        when(userCreation.getUserId()).thenReturn(userId);
        final ScimGroupMember scimGroupMemberResponse = mock(ScimGroupMember.class);
        when(restTemplate.postForObject(eq(groupsEndpoint + "/{groupId}/members"),
                argThat(matching(
                        (ScimGroupMember member) -> userId.equals(member.getMemberId())
                )),
                eq(ScimGroupMember.class), eq(groupId))).thenReturn(scimGroupMemberResponse);

        // Act
        final ScimGroupMember response = sut.addUserToGroup(userCreation, scope1, groupId);

        // Assert
        assertEquals(scimGroupMemberResponse, response);
        verify(userScopeAssignmentRepository, times(1)).save(argThat(matching(
                (UserScopeAssignment userScopeAssignment) ->
                        userScopeAssignment.getScope().equals(scope1) && userScopeAssignment.getUserCreation().equals(userCreation)
        )));
    }

    @Test
    public void testAddUserToGroup_Scope2() throws Exception {
        // Arrange
        final String userId = "userId";
        final String groupId = "groupId";
        final UserCreation userCreation = mock(UserCreation.class);
        final UserType userType = mock(UserType.class);
        final Scope scope1 = mock(Scope.class);
        final Scope scope2 = mock(Scope.class);
        when(userType.getScopes()).thenReturn(Arrays.asList(scope1, scope2));
        when(userCreation.getUserType()).thenReturn(userType);
        when(userCreation.getUserId()).thenReturn(userId);
        final ScimGroupMember scimGroupMemberResponse = mock(ScimGroupMember.class);
        when(restTemplate.postForObject(eq(groupsEndpoint + "/{groupId}/members"),
                argThat(matching(
                        (ScimGroupMember member) -> userId.equals(member.getMemberId())
                )),
                eq(ScimGroupMember.class), eq(groupId))).thenReturn(scimGroupMemberResponse);

        // Act
        final ScimGroupMember response = sut.addUserToGroup(userCreation, scope2, groupId);

        // Assert
        assertEquals(scimGroupMemberResponse, response);
        verify(userScopeAssignmentRepository, times(1)).save(argThat(matching(
                (UserScopeAssignment userScopeAssignment) ->
                        userScopeAssignment.getScope().equals(scope2) && userScopeAssignment.getUserCreation().equals(userCreation)
        )));
    }

    @Test
    public void testAddUserToGroups() throws Exception {
        // Arrange
        final String userId = "userId";
        final String groupId1 = "groupId1";
        final String groupId2 = "groupId2";
        final String groupDisplayName1 = "groupDisplayName1";
        final String groupDisplayName2 = "groupDisplayName2";
        final UserCreation userCreation = mock(UserCreation.class);
        final UserType userType = mock(UserType.class);
        final Scope scope1 = mock(Scope.class);
        when(scope1.getScope()).thenReturn(groupDisplayName1);
        final Scope scope2 = mock(Scope.class);
        when(scope2.getScope()).thenReturn(groupDisplayName2);
        when(userType.getScopes()).thenReturn(Arrays.asList(scope1, scope2));
        when(userCreation.getUserType()).thenReturn(userType);
        when(userCreation.getUserId()).thenReturn(userId);
        final ScimGroupMember scimGroupMemberResponse1 = mock(ScimGroupMember.class);
        final ScimGroupMember scimGroupMemberResponse2 = mock(ScimGroupMember.class);
        when(restTemplate.postForObject(eq(groupsEndpoint + "/{groupId}/members"),
                argThat(matching(
                        (ScimGroupMember member) -> userId.equals(member.getMemberId())
                )),
                eq(ScimGroupMember.class), eq(groupId1))).thenReturn(scimGroupMemberResponse1);
        when(restTemplate.postForObject(eq(groupsEndpoint + "/{groupId}/members"),
                argThat(matching(
                        (ScimGroupMember member) -> userId.equals(member.getMemberId())
                )),
                eq(ScimGroupMember.class), eq(groupId2))).thenReturn(scimGroupMemberResponse2);
        final String id1 = "id1";
        final String id2 = "id2";
        final SearchResultsWrapperWithId searchResultsMock1 = mock(SearchResultsWrapperWithId.class);
        final IdentifierDto identifier1 = new IdentifierDto();
        identifier1.setId(id1);
        final SearchResultsWrapperWithId searchResultsMock2 = mock(SearchResultsWrapperWithId.class);
        final IdentifierDto identifier2 = new IdentifierDto();
        identifier2.setId(id2);
        when(searchResultsMock1.getResources()).thenReturn(Arrays.asList(identifier1));
        when(searchResultsMock2.getResources()).thenReturn(Arrays.asList(identifier2));
        when(restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName1 + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock1);
        when(restTemplate.getForObject(groupsEndpoint + "?filter=displayName eq \"" + groupDisplayName2 + "\"&attributes=id", SearchResultsWrapperWithId.class)).thenReturn(searchResultsMock2);


        // Act
        sut.addUserToGroups(userCreation);

        // Assert
        verify(userScopeAssignmentRepository, times(1)).save(argThat(matching(
                (UserScopeAssignment userScopeAssignment) ->
                        userScopeAssignment.getScope().equals(scope1) && userScopeAssignment.getUserCreation().equals(userCreation)
        )));
        verify(userScopeAssignmentRepository, times(1)).save(argThat(matching(
                (UserScopeAssignment userScopeAssignment) ->
                        userScopeAssignment.getScope().equals(scope2) && userScopeAssignment.getUserCreation().equals(userCreation)
        )));
    }
}