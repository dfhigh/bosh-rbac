package com.bosh.rbac.service;

import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.auth.model.ResourceAccessAuth;
import com.bosh.rbac.context.RbacContext;
import com.bosh.rbac.context.RbacScope;
import com.bosh.rbac.mapper.RbacMapper;
import com.bosh.rbac.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class AuthorizerTest {

    @Mock
    private RbacContext context;
    @Mock
    private User user;
    @Mock
    private Role role;
    @Mock
    private ResourceAccess resourceAccess;
    @Mock
    private Resource resource1, resource2, resource3;
    @Mock
    private Policy policy1, policy2, policy3;
    @Mock
    private EntityPolicy entityPolicy;
    @MockBean
    private RbacMapper mapper;
    @MockBean
    private PolicyPredicate predicate;

    @Autowired
    private Authorizer authorizer;

    @BeforeEach
    void setup() {
        RbacScope.setContext(context);
        when(context.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(resourceAccess.getResource()).thenReturn(resource1);
        when(resourceAccess.getAction()).thenReturn(Action.Read);
        when(resourceAccess.getDecoratedResources()).thenReturn(Lists.newArrayList(resource2, resource3));
        when(resource1.getType()).thenReturn(ResourceType.HDFS);
        when(resource1.getValue()).thenReturn("hdfs://localhost:8020/home/work/data");
        when(resource2.getType()).thenReturn(ResourceType.HDFS);
        when(resource2.getValue()).thenReturn("hdfs://localhost:8020/home/work");
        when(resource3.getType()).thenReturn(ResourceType.HDFS);
        when(resource3.getValue()).thenReturn("hdfs://localhost:8020/home");
        when(mapper.getResourcesPolicies(eq(Lists.newArrayList(resource1, resource2, resource3)))).thenReturn(Lists.newArrayList(policy1, policy2, policy3));
        when(predicate.pass(policy1, resourceAccess)).thenReturn(true);
        when(predicate.pass(policy2, resourceAccess)).thenReturn(false);
        when(predicate.pass(policy3, resourceAccess)).thenReturn(true);
        when(policy1.getId()).thenReturn(1L);
        when(policy2.getId()).thenReturn(2L);
        when(policy3.getId()).thenReturn(3L);
    }

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));

        when(resourceAccess.getAction()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));

        when(resourceAccess.getAction()).thenReturn(Action.Read);
        when(resourceAccess.getResource()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));

        when(resourceAccess.getResource()).thenReturn(resource1);
        when(resource1.getType()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));

        when(resource1.getType()).thenReturn(ResourceType.HDFS);
        when(resource1.getValue()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));

        when(resource1.getValue()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));

        when(resource1.getValue()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> authorizer.authorize(null));
    }

    @Test
    void testAuthorizeAllowedByUser() {
        when(mapper.getUserPolicy(eq(1L), eq(Lists.newArrayList(1L, 3L)))).thenReturn(entityPolicy);
        when(entityPolicy.getEntityType()).thenReturn(EntityType.User);
        when(entityPolicy.getPolicyId()).thenReturn(3L);

        ResourceAccessAuth raa = authorizer.authorize(resourceAccess);

        assertNotNull(raa);
        assertTrue(raa.isAllow());
        assertEquals(user, raa.getAllowedAsEntity());
        assertEquals(policy3, raa.getAllowedByPolicy());
        assertEquals(resourceAccess, raa.getResourceAccess());
        verify(mapper).getResourcesPolicies(eq(Lists.newArrayList(resource1, resource2, resource3)));
        verify(predicate).pass(policy1, resourceAccess);
        verify(predicate).pass(policy2, resourceAccess);
        verify(predicate).pass(policy3, resourceAccess);
        verify(mapper).getUserPolicy(eq(1L), eq(Lists.newArrayList(1L, 3L)));
        verify(mapper, never()).getRole(anyLong());
    }

    @Test
    void testAuthorizeAllowedByRole() {
        when(mapper.getUserPolicy(eq(1L), eq(Lists.newArrayList(1L, 3L)))).thenReturn(entityPolicy);
        when(entityPolicy.getEntityType()).thenReturn(EntityType.Role);
        when(entityPolicy.getEntityId()).thenReturn(1L);
        when(entityPolicy.getPolicyId()).thenReturn(1L);
        when(mapper.getRole(1L)).thenReturn(role);

        ResourceAccessAuth raa = authorizer.authorize(resourceAccess);

        assertNotNull(raa);
        assertTrue(raa.isAllow());
        assertEquals(role, raa.getAllowedAsEntity());
        assertEquals(policy1, raa.getAllowedByPolicy());
        assertEquals(resourceAccess, raa.getResourceAccess());
        verify(mapper).getResourcesPolicies(eq(Lists.newArrayList(resource1, resource2, resource3)));
        verify(predicate).pass(policy1, resourceAccess);
        verify(predicate).pass(policy2, resourceAccess);
        verify(predicate).pass(policy3, resourceAccess);
        verify(mapper).getUserPolicy(eq(1L), eq(Lists.newArrayList(1L, 3L)));
        verify(mapper).getRole(1L);
    }

    @Test
    void testAuthorizeDenied() {
        when(mapper.getUserPolicy(eq(1L), eq(Lists.newArrayList(1L, 3L)))).thenReturn(null);

        ResourceAccessAuth raa = authorizer.authorize(resourceAccess);

        assertNotNull(raa);
        assertFalse(raa.isAllow());
        assertNull(raa.getAllowedAsEntity());
        assertNull(raa.getAllowedByPolicy());
        assertEquals(resourceAccess, raa.getResourceAccess());
        verify(mapper).getResourcesPolicies(eq(Lists.newArrayList(resource1, resource2, resource3)));
        verify(predicate).pass(policy1, resourceAccess);
        verify(predicate).pass(policy2, resourceAccess);
        verify(predicate).pass(policy3, resourceAccess);
        verify(mapper).getUserPolicy(eq(1L), eq(Lists.newArrayList(1L, 3L)));
        verify(mapper, never()).getRole(anyLong());
    }
}
