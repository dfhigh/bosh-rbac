package com.bosh.rbac.service;

import com.bosh.rbac.mapper.RbacMapper;
import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.model.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.mib.rest.exception.ResourceNotFoundException;
import org.mib.rest.model.list.Filter;
import org.mib.rest.model.list.ListElementRequest;
import org.mib.rest.model.list.ListPayload;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
@SuppressWarnings("unchecked")
class EntityReadServiceTest {

    @MockBean
    private RbacMapper mapper;

    @Autowired
    private EntityReadService service;

    @Test
    void testGetUserNotFound() {
        when(mapper.getUser("user")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> service.getUser("user"));
    }

    @Test
    void testGetUser() {
        String userId = "user";
        User user = mock(User.class);
        when(mapper.getUser(userId)).thenReturn(user);
        assertEquals(user, service.getUser(userId));
        verify(mapper).getUser(userId);
    }

    @Test
    void testListUsersNoSearch() {
        List<User> users = (List<User>) mock(List.class);
        when(mapper.listUsers(any(ListElementRequest.class))).thenReturn(users);
        when(mapper.countUsers(any(ListElementRequest.class))).thenReturn(10L);
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);

        ListPayload<User> lp = service.listUsers(0, 10, null);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(users, lp.getList());
        assertEquals(10, lp.getTotal());
        verify(mapper).listUsers(lerCaptor.capture());
        ListElementRequest ler = lerCaptor.getValue();
        verify(mapper).countUsers(ler);
        assertNotNull(ler);
        assertEquals(0, ler.getOffset());
        assertEquals(10, ler.getLimit());
        assertNull(ler.getFilters());
    }

    @Test
    void testListUsersWithSearch() {
        List<User> users = (List<User>) mock(List.class);
        when(mapper.listUsers(any(ListElementRequest.class))).thenReturn(users);
        when(mapper.countUsers(any(ListElementRequest.class))).thenReturn(10L);
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);

        String search = "search";
        ListPayload<User> lp = service.listUsers(0, 10, search);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(users, lp.getList());
        assertEquals(10, lp.getTotal());
        verify(mapper).listUsers(lerCaptor.capture());
        ListElementRequest ler = lerCaptor.getValue();
        assertNotNull(ler);
        assertEquals(0, ler.getOffset());
        assertEquals(10, ler.getLimit());
        assertNotNull(ler.getFilters());
        assertEquals(Lists.newArrayList(Filter.like("username", "%search%")), ler.getFilters());
    }

    @Test
    void testListRoleUsers() {
        List<User> users = (List<User>) mock(List.class);
        when(mapper.listRoleUsers(1, 0, 10)).thenReturn(users);
        when(mapper.countRoleUsers(1)).thenReturn(10L);

        ListPayload<User> lp = service.listRoleUsers(1, 0, 10);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(10, lp.getTotal());
        assertEquals(users, lp.getList());
        verify(mapper).listRoleUsers(1, 0, 10);
        verify(mapper).countRoleUsers(1);
    }

    @Test
    void testGetRoleNotFound() {
        when(mapper.getRole(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> service.getRole(1));
    }

    @Test
    void testGetRole() {
        long roleId = 1;
        Role role = mock(Role.class);
        when(mapper.getRole(roleId)).thenReturn(role);
        assertEquals(role, service.getRole(roleId));
        verify(mapper).getRole(roleId);
    }

    @Test
    void testListRolesNoSearch() {
        List<Role> roles = (List<Role>) mock(List.class);
        when(mapper.listRoles(any(ListElementRequest.class))).thenReturn(roles);
        when(mapper.countRoles(any(ListElementRequest.class))).thenReturn(10L);
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);

        ListPayload<Role> lp = service.listRoles(0, 10, null);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(roles, lp.getList());
        assertEquals(10, lp.getTotal());
        verify(mapper).listRoles(lerCaptor.capture());
        ListElementRequest ler = lerCaptor.getValue();
        verify(mapper).countRoles(ler);
        assertNotNull(ler);
        assertEquals(0, ler.getOffset());
        assertEquals(10, ler.getLimit());
        assertNull(ler.getFilters());
    }

    @Test
    void testListRolesWithSearch() {
        List<Role> roles = (List<Role>) mock(List.class);
        when(mapper.listRoles(any(ListElementRequest.class))).thenReturn(roles);
        when(mapper.countRoles(any(ListElementRequest.class))).thenReturn(10L);
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);

        String search = "search";
        ListPayload<Role> lp = service.listRoles(0, 10, search);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(roles, lp.getList());
        assertEquals(10, lp.getTotal());
        verify(mapper).listRoles(lerCaptor.capture());
        ListElementRequest ler = lerCaptor.getValue();
        assertNotNull(ler);
        assertEquals(0, ler.getOffset());
        assertEquals(10, ler.getLimit());
        assertNotNull(ler.getFilters());
        assertEquals(Lists.newArrayList(Filter.like("name", "%search%")), ler.getFilters());
    }

    @Test
    void testListUserRoles() {
        List<Role> roles = (List<Role>) mock(List.class);
        when(mapper.listUserRoles(1, 0, 10)).thenReturn(roles);
        when(mapper.countUserRoles(1)).thenReturn(10L);

        ListPayload<Role> lp = service.listUserRoles(1, 0, 10);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(10, lp.getTotal());
        assertEquals(roles, lp.getList());
        verify(mapper).listUserRoles(1, 0, 10);
        verify(mapper).countUserRoles(1);
    }

    @Test
    void testGetPolicyNotFound() {
        when(mapper.getPolicy(1)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> service.getPolicy(1));
    }

    @Test
    void testGetPolicy() {
        long policyId = 1;
        Policy policy = mock(Policy.class);
        when(mapper.getPolicy(policyId)).thenReturn(policy);
        assertEquals(policy, service.getPolicy(policyId));
        verify(mapper).getPolicy(policyId);
    }

    @Test
    void testListPoliciesNoSearch() {
        List<Policy> policies = (List<Policy>) mock(List.class);
        when(mapper.listPolicies(any(ListElementRequest.class))).thenReturn(policies);
        when(mapper.countPolicies(any(ListElementRequest.class))).thenReturn(10L);
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);

        ListPayload<Policy> lp = service.listPolicies(0, 10, null);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(policies, lp.getList());
        assertEquals(10, lp.getTotal());
        verify(mapper).listPolicies(lerCaptor.capture());
        ListElementRequest ler = lerCaptor.getValue();
        verify(mapper).countPolicies(ler);
        assertNotNull(ler);
        assertEquals(0, ler.getOffset());
        assertEquals(10, ler.getLimit());
        assertNull(ler.getFilters());
    }

    @Test
    void testListPoliciesWithSearch() {
        List<Policy> policies = (List<Policy>) mock(List.class);
        when(mapper.listPolicies(any(ListElementRequest.class))).thenReturn(policies);
        when(mapper.countPolicies(any(ListElementRequest.class))).thenReturn(10L);
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);

        String search = "search";
        ListPayload<Policy> lp = service.listPolicies(0, 10, search);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(policies, lp.getList());
        assertEquals(10, lp.getTotal());
        verify(mapper).listPolicies(lerCaptor.capture());
        ListElementRequest ler = lerCaptor.getValue();
        verify(mapper).listPolicies(ler);
        assertNotNull(ler);
        assertEquals(0, ler.getOffset());
        assertEquals(10, ler.getLimit());
        assertNotNull(ler.getFilters());
        assertEquals(Lists.newArrayList(Filter.like("name", "%search%")), ler.getFilters());
    }

    @Test
    void testListEntityPolicies() {
        Entity entity = mock(Entity.class);
        List<Policy> policies = (List<Policy>) mock(List.class);
        when(mapper.listEntityPolicies(entity, 0, 10)).thenReturn(policies);
        when(mapper.countEntityPolicies(entity)).thenReturn(10L);

        ListPayload<Policy> lp = service.listEntityPolicies(entity, 0, 10);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(10, lp.getTotal());
        assertEquals(policies, lp.getList());
        verify(mapper).listEntityPolicies(entity, 0, 10);
        verify(mapper).countEntityPolicies(entity);
    }

    @Test
    void testListPolicyEntities() {
        Entity e1 = new Entity(EntityType.Role, 1), e2 = new Entity(EntityType.User, 1);
        User user = mock(User.class);
        Role role = mock(Role.class);
        when(user.getId()).thenReturn(1L);
        when(role.getId()).thenReturn(1L);
        when(mapper.listPolicyEntities(1, 0, 2)).thenReturn(Lists.newArrayList(e1, e2));
        when(mapper.countPolicyEntities(1)).thenReturn(10L);
        when(mapper.listUsers(any(ListElementRequest.class))).thenReturn(Lists.newArrayList(user));
        when(mapper.listRoles(any(ListElementRequest.class))).thenReturn(Lists.newArrayList(role));

        ListPayload<Entity> lp = service.listPolicyEntities(1, 0, 2);

        assertNotNull(lp);
        assertEquals(0, lp.getOffset());
        assertEquals(10, lp.getTotal());
        assertEquals(Lists.newArrayList(role, user), lp.getList());
        ArgumentCaptor<ListElementRequest> lerCaptor = ArgumentCaptor.forClass(ListElementRequest.class);
        verify(mapper).listPolicyEntities(1, 0, 2);
        verify(mapper).listUsers(lerCaptor.capture());
        verify(mapper).listRoles(lerCaptor.capture());
        List<ListElementRequest> lerList = lerCaptor.getAllValues();
        assertNotNull(lerList);
        assertEquals(2, lerList.size());
        ListElementRequest ler1 = lerList.get(0), ler2 = lerList.get(1);
        assertNotNull(ler1);
        assertEquals(0, ler1.getOffset());
        assertEquals(1, ler1.getLimit());
        assertNotNull(ler1.getIns());
        assertEquals(1, ler1.getIns().size());
        assertEquals(Sets.newHashSet(1L), ler1.getIns().get("id"));
        assertNotNull(ler2);
        assertEquals(0, ler2.getOffset());
        assertEquals(1, ler2.getLimit());
        assertNotNull(ler1.getIns());
        assertEquals(1, ler1.getIns().size());
        assertEquals(Sets.newHashSet(1L), ler1.getIns().get("id"));
    }
}
