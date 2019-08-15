package com.bosh.rbac.service;

import com.bosh.rbac.context.RbacContext;
import com.bosh.rbac.context.RbacScope;
import com.bosh.rbac.mapper.RbacMapper;
import com.bosh.rbac.model.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mib.rest.exception.BadRequestException;
import org.mib.rest.exception.ForbiddenException;
import org.mib.rest.exception.ResourceNotFoundException;
import org.mib.rest.exception.UnauthorizedException;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class AdminServiceTest {

    @Mock
    private User user;
    @Mock
    private Role role;
    @Mock
    private Policy policy;
    @Mock
    private Resource resource;
    @Mock
    private RbacContext context;
    @MockBean
    private RbacMapper mapper;

    @Autowired
    private AdminService admin;

    @BeforeEach
    void setupCase() {
        when(context.getUser()).thenReturn(user);
        when(user.isAdmin()).thenReturn(true);
        RbacScope.setContext(context);
    }

    @Test
    void testNoUserInContext() {
        when(context.getUser()).thenReturn(null);
        assertThrows(UnauthorizedException.class, () -> admin.createRole(null));
    }

    @Test
    void testUserNotAdmin() {
        when(user.isAdmin()).thenReturn(false);
        assertThrows(ForbiddenException.class, () -> admin.createRole(null));
    }

    @Test
    void testInvalidRoleCreate() {
        assertThrows(IllegalArgumentException.class, () -> admin.createRole(null));

        when(role.getName()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> admin.createRole(role));

        when(role.getName()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> admin.createRole(role));

        when(role.getName()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> admin.createRole(role));
    }

    @Test
    void testRoleCreate() {
        when(role.getName()).thenReturn("role");
        assertEquals(role, admin.createRole(role));
        verify(role).setCreatedAt(any(Date.class));
        verify(role).setUpdatedAt(any(Date.class));
        verify(mapper).insertRole(role);
    }

    @Test
    void testInvalidRoleDescriptionUpdate() {
        assertThrows(IllegalArgumentException.class, () -> admin.updateRoleDescription(0, "d"));
        assertThrows(IllegalArgumentException.class, () -> admin.updateRoleDescription(1, null));
        assertThrows(IllegalArgumentException.class, () -> admin.updateRoleDescription(1, ""));
        assertThrows(IllegalArgumentException.class, () -> admin.updateRoleDescription(1, " "));
    }

    @Test
    void testNonExistRoleDescriptionUpdate() {
        when(mapper.getRole(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.updateRoleDescription(1, "d"));
    }

    @Test
    void testRoleDescriptionUpdateFailure() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.updateRole(role)).thenReturn(0);
        assertThrows(RuntimeException.class, () -> admin.updateRoleDescription(1, "d"));
    }

    @Test
    void testRoleDescriptionUpdate() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.updateRole(role)).thenReturn(1);
        assertEquals(role, admin.updateRoleDescription(1, "d"));
        verify(mapper).getRole(1);
        verify(role).setUpdatedAt(any(Date.class));
        verify(role).setDescription("d");
    }

    @Test
    void testInvalidRoleDelete() {
        assertThrows(IllegalArgumentException.class, () -> admin.deleteRole(0));

        when(mapper.countRoleUsers(1)).thenReturn(1L);
        assertThrows(BadRequestException.class, () -> admin.deleteRole(1));

        when(mapper.countRoleUsers(1)).thenReturn(0L);
        when(mapper.countEntityPolicies(eq(new Entity(EntityType.Role, 1)))).thenReturn(1L);
        assertThrows(BadRequestException.class, () -> admin.deleteRole(1));
    }

    @Test
    void testRoleDelete() {
        Entity entity = new Entity(EntityType.Role, 1);
        when(mapper.countRoleUsers(1)).thenReturn(0L);
        when(mapper.countEntityPolicies(eq(entity))).thenReturn(0L);
        admin.deleteRole(1);
        verify(mapper).countRoleUsers(1);
        verify(mapper).countEntityPolicies(eq(entity));
        verify(mapper).deleteRoles(eq(Lists.newArrayList(1L)));
    }

    @Test
    void testInvalidPolicyCreate() {
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(null));

        when(policy.getName()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(policy.getName()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(policy.getName()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(policy.getName()).thenReturn("policy");
        when(policy.getResource()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(policy.getResource()).thenReturn(resource);
        when(resource.getType()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(resource.getType()).thenReturn(ResourceType.HDFS);
        when(resource.getValue()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(resource.getValue()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(resource.getValue()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));

        when(resource.getValue()).thenReturn("resource");
        when(policy.getAction()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> admin.createPolicy(policy));
    }

    @Test
    void testPolicyCreate() {
        when(policy.getName()).thenReturn("policy");
        when(policy.getResource()).thenReturn(resource);
        when(resource.getType()).thenReturn(ResourceType.HDFS);
        when(resource.getValue()).thenReturn("resource");
        when(policy.getAction()).thenReturn(Action.Write);
        assertEquals(policy, admin.createPolicy(policy));
        verify(policy).setCreatedAt(any(Date.class));
        verify(policy).setUpdatedAt(any(Date.class));
        verify(mapper).insertPolicy(policy);
    }

    @Test
    void testInvalidPolicyDescriptionUpdate() {
        assertThrows(IllegalArgumentException.class, () -> admin.updatePolicyDescription(0, "d"));
        assertThrows(IllegalArgumentException.class, () -> admin.updatePolicyDescription(1, null));
        assertThrows(IllegalArgumentException.class, () -> admin.updatePolicyDescription(1, ""));
        assertThrows(IllegalArgumentException.class, () -> admin.updatePolicyDescription(1, " "));
    }

    @Test
    void testNonExistPolicyDescriptionUpdate() {
        when(mapper.getPolicy(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.updatePolicyDescription(1, "d"));
    }

    @Test
    void testPolicyDescriptionUpdateFailure() {
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.updatePolicy(policy)).thenReturn(0);
        assertThrows(RuntimeException.class, () -> admin.updatePolicyDescription(1, "d"));
    }

    @Test
    void testPolicyDescriptionUpdate() {
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.updatePolicy(policy)).thenReturn(1);
        assertEquals(policy, admin.updatePolicyDescription(1, "d"));
        verify(mapper).getPolicy(1);
        verify(policy).setUpdatedAt(any(Date.class));
        verify(policy).setDescription("d");
    }

    @Test
    void testInvalidPolicyDelete() {
        assertThrows(IllegalArgumentException.class, () -> admin.deletePolicy(0));

        when(mapper.countPolicyEntities(1)).thenReturn(1L);
        assertThrows(BadRequestException.class, () -> admin.deletePolicy(1));
    }

    @Test
    void testPolicyDelete() {
        when(mapper.countPolicyEntities(1)).thenReturn(0L);
        admin.deletePolicy(1);
        verify(mapper).countPolicyEntities(1);
        verify(mapper).deletePolicies(eq(Lists.newArrayList(1L)));
    }

    @Test
    void testInvalidRoleAssignment() {
        assertThrows(IllegalArgumentException.class, () -> admin.assignRoleToUser(null, 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignRoleToUser("", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignRoleToUser(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignRoleToUser("user", 0));

        when(mapper.getUser("user")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.assignRoleToUser("user", 1));

        when(mapper.getUser("user")).thenReturn(user);
        when(mapper.getRole(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.assignRoleToUser("user", 1));
    }

    @Test
    void testDuplicateRoleAssignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getUserRole(1, 1)).thenReturn(mock(UserRole.class));

        admin.assignRoleToUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getRole(1);
        verify(mapper).getUserRole(1, 1);
        verify(mapper, never()).insertUserRole(anyLong(), anyLong());
    }

    @Test
    void testRoleAssignmentFailure() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getUserRole(1, 1)).thenReturn(null);
        when(mapper.insertUserRole(1, 1)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> admin.assignRoleToUser("user", 1));
    }

    @Test
    void testRoleAssignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getUserRole(1, 1)).thenReturn(null);
        when(mapper.insertUserRole(1, 1)).thenReturn(1);

        admin.assignRoleToUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getRole(1);
        verify(mapper).getUserRole(1, 1);
        verify(mapper).insertUserRole(1, 1);
    }

    @Test
    void testInvalidRoleUnassignment() {
        assertThrows(IllegalArgumentException.class, () -> admin.unassignRoleFromUser(null, 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignRoleFromUser("", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignRoleFromUser(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignRoleFromUser("user", 0));

        when(mapper.getUser("user")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.unassignRoleFromUser("user", 1));

        when(mapper.getUser("user")).thenReturn(user);
        when(mapper.getRole(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.unassignRoleFromUser("user", 1));
    }

    @Test
    void testDuplicateRoleUnassignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getUserRole(1, 1)).thenReturn(null);

        admin.unassignRoleFromUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getRole(1);
        verify(mapper).getUserRole(1, 1);
        verify(mapper, never()).deleteUserRole(anyLong(), anyLong());
    }

    @Test
    void testRoleUnassignmentFailure() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getUserRole(1, 1)).thenReturn(mock(UserRole.class));
        when(mapper.deleteUserRole(1, 1)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> admin.unassignRoleFromUser("user", 1));
    }

    @Test
    void testRoleUnassignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getUserRole(1, 1)).thenReturn(mock(UserRole.class));
        when(mapper.deleteUserRole(1, 1)).thenReturn(1);

        admin.unassignRoleFromUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getRole(1);
        verify(mapper).getUserRole(1, 1);
        verify(mapper).deleteUserRole(1, 1);
    }

    @Test
    void testInvalidPolicyToUserAssignment() {
        assertThrows(IllegalArgumentException.class, () -> admin.assignPolicyToUser(null, 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignPolicyToUser("", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignPolicyToUser(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignPolicyToUser("user", 0));

        when(mapper.getUser("user")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.assignPolicyToUser("user", 1));

        when(mapper.getUser("user")).thenReturn(user);
        when(mapper.getPolicy(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.assignPolicyToUser("user", 1));
    }

    @Test
    void testDuplicatePolicyToUserAssignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.User, 1, 1)).thenReturn(mock(EntityPolicy.class));

        admin.assignPolicyToUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.User, 1, 1);
        verify(mapper, never()).insertEntityPolicy(any(EntityType.class), anyLong(), anyLong());
    }

    @Test
    void testPolicyToUserAssignmentFailure() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.User, 1, 1)).thenReturn(null);
        when(mapper.insertEntityPolicy(EntityType.User, 1, 1)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> admin.assignPolicyToUser("user", 1));
    }

    @Test
    void testPolicyToUserAssignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.User, 1, 1)).thenReturn(null);
        when(mapper.insertEntityPolicy(EntityType.User, 1, 1)).thenReturn(1);

        admin.assignPolicyToUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.User, 1, 1);
        verify(mapper).insertEntityPolicy(EntityType.User, 1, 1);
    }

    @Test
    void testInvalidPolicyToUserUnassignment() {
        assertThrows(IllegalArgumentException.class, () -> admin.unassignPolicyFromUser(null, 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignPolicyFromUser("", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignPolicyFromUser(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignPolicyFromUser("user", 0));

        when(mapper.getUser("user")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.unassignPolicyFromUser("user", 1));

        when(mapper.getUser("user")).thenReturn(user);
        when(mapper.getPolicy(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.unassignPolicyFromUser("user", 1));
    }

    @Test
    void testDuplicatePolicyToUserUnassignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.User, 1, 1)).thenReturn(null);

        admin.unassignPolicyFromUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.User, 1, 1);
        verify(mapper, never()).deleteEntityPolicy(any(EntityType.class), anyLong(), anyLong());
    }

    @Test
    void testPolicyToUserUnassignmentFailure() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.User, 1, 1)).thenReturn(mock(EntityPolicy.class));
        when(mapper.deleteEntityPolicy(EntityType.User, 1, 1)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> admin.unassignPolicyFromUser("user", 1));
    }

    @Test
    void testPolicyToUserUnassignment() {
        when(mapper.getUser("user")).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.User, 1, 1)).thenReturn(mock(EntityPolicy.class));
        when(mapper.deleteEntityPolicy(EntityType.User, 1, 1)).thenReturn(1);

        admin.unassignPolicyFromUser("user", 1);

        verify(mapper).getUser("user");
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.User, 1, 1);
        verify(mapper).deleteEntityPolicy(EntityType.User, 1, 1);
    }

    @Test
    void testInvalidPolicyToRoleAssignment() {
        assertThrows(IllegalArgumentException.class, () -> admin.assignPolicyToRole(0, 1));
        assertThrows(IllegalArgumentException.class, () -> admin.assignPolicyToRole(1, 0));

        when(mapper.getRole(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.assignPolicyToRole(1, 1));

        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.assignPolicyToRole(1, 1));
    }

    @Test
    void testDuplicatePolicyToRoleAssignment() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.Role, 1, 1)).thenReturn(mock(EntityPolicy.class));

        admin.assignPolicyToRole(1, 1);

        verify(mapper).getRole(1);
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.Role, 1, 1);
        verify(mapper, never()).insertEntityPolicy(any(EntityType.class), anyLong(), anyLong());
    }

    @Test
    void testPolicyToRoleAssignmentFailure() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.Role, 1, 1)).thenReturn(null);
        when(mapper.insertEntityPolicy(EntityType.Role, 1, 1)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> admin.assignPolicyToRole(1, 1));
    }

    @Test
    void testPolicyToRoleAssignment() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.Role, 1, 1)).thenReturn(null);
        when(mapper.insertEntityPolicy(EntityType.Role, 1, 1)).thenReturn(1);

        admin.assignPolicyToRole(1, 1);

        verify(mapper).getRole(1);
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.Role, 1, 1);
        verify(mapper).insertEntityPolicy(EntityType.Role, 1, 1);
    }

    @Test
    void testInvalidPolicyToRoleUnassignment() {
        assertThrows(IllegalArgumentException.class, () -> admin.unassignPolicyFromRole(0, 1));
        assertThrows(IllegalArgumentException.class, () -> admin.unassignPolicyFromRole(1, 0));

        when(mapper.getRole(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.unassignPolicyFromRole(1, 1));

        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> admin.unassignPolicyFromRole(1, 1));
    }

    @Test
    void testDuplicatePolicyToRoleUnassignment() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.Role, 1, 1)).thenReturn(null);

        admin.unassignPolicyFromRole(1, 1);

        verify(mapper).getRole(1);
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.Role, 1, 1);
        verify(mapper, never()).deleteEntityPolicy(any(EntityType.class), anyLong(), anyLong());
    }

    @Test
    void testPolicyToRoleUnassignmentFailure() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.Role, 1, 1)).thenReturn(mock(EntityPolicy.class));
        when(mapper.deleteEntityPolicy(EntityType.Role, 1, 1)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> admin.unassignPolicyFromRole(1, 1));
    }

    @Test
    void testPolicyToRoleUnassignment() {
        when(mapper.getRole(1)).thenReturn(role);
        when(mapper.getPolicy(1)).thenReturn(policy);
        when(mapper.getEntityPolicy(EntityType.Role, 1, 1)).thenReturn(mock(EntityPolicy.class));
        when(mapper.deleteEntityPolicy(EntityType.Role, 1, 1)).thenReturn(1);

        admin.unassignPolicyFromRole(1, 1);

        verify(mapper).getRole(1);
        verify(mapper).getPolicy(1);
        verify(mapper).getEntityPolicy(EntityType.Role, 1, 1);
        verify(mapper).deleteEntityPolicy(EntityType.Role, 1, 1);
    }
}
