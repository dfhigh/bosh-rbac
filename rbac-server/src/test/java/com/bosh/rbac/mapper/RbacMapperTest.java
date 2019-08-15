package com.bosh.rbac.mapper;

import com.bosh.rbac.model.*;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mib.rest.model.list.Filter;
import org.mib.rest.model.list.ListElementRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("ut")
class RbacMapperTest {

    private static final Date DATE = Date.from(LocalDateTime.parse("2017-10-20T00:10:30").atZone(ZoneId.systemDefault()).toInstant());

    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private RbacMapper mapper;

    @BeforeEach
    void setup() {
        jdbc.execute("insert into `users` values" +
                "(1, 'uId1', 'user1', 0, timestamp '2017-10-20 00:10:30')," +
                "(2, 'uId2', 'user2', 0, timestamp '2017-10-20 00:10:30')," +
                "(3, 'uId3', 'user3', 0, timestamp '2017-10-20 00:10:30')");
        jdbc.execute("insert into `roles` values" +
                "(1, 'admin', 'administrator', timestamp '2017-10-20 00:10:30', timestamp '2017-10-20 00:10:30')," +
                "(2, 'analyst', 'read only', timestamp '2017-10-20 00:10:30', timestamp '2017-10-20 00:10:30')");
        jdbc.execute("insert into `policies` values" +
                "(1, 'hdfs_reader', 'hdfs read only role', 1, 'hdfs://host:8020/data', 1, timestamp '2017-10-20 00:10:30', timestamp '2017-10-20 00:10:30')," +
                "(2, 'hdfs_writer', 'hdfs write role', 1, 'hdfs://host:8020/data', 2, timestamp '2017-10-20 00:10:30', timestamp '2017-10-20 00:10:30')," +
                "(3, 'tag_reader', 'tag read only role', 3, 'tag1', 1, timestamp '2017-10-20 00:10:30', timestamp '2017-10-20 00:10:30')," +
                "(4, 'tag_writer', 'tag write role', 3, 'tag1', 2, timestamp '2017-10-20 00:10:30', timestamp '2017-10-20 00:10:30')");
        jdbc.execute("insert into `users_roles` values" +
                "(1, 1, timestamp '2017-10-20 00:10:30')," +
                "(2, 2, timestamp '2017-10-20 00:10:30')");
        jdbc.execute("insert into `entities_policies` values" +
                "(2, 1, 2, timestamp '2017-10-20 00:10:30')," +
                "(2, 1, 4, timestamp '2017-10-20 00:10:30')," +
                "(2, 2, 1, timestamp '2017-10-20 00:10:30')," +
                "(2, 2, 3, timestamp '2017-10-20 00:10:30')," +
                "(1, 3, 1, timestamp '2017-10-20 00:10:30')," +
                "(1, 3, 4, timestamp '2017-10-20 00:10:30')");
    }

    @AfterEach
    void destroy() {
        jdbc.execute("delete from `users`");
        jdbc.execute("delete from `roles`");
        jdbc.execute("delete from `policies`");
        jdbc.execute("delete from `users_roles`");
        jdbc.execute("delete from `entities_policies`");
    }

    @Test
    void testUserRead() {
        User user1 = mapper.getUser("uId1"), user2 = mapper.getUser("uId2"), user3 = mapper.getUser("uId3");
        validateUser(user1, 1, "uId1", "user1", false, DATE);
        validateUser(user2, 2, "uId2", "user2", false, DATE);
        validateUser(user3, 3, "uId3", "user3", false, DATE);
        assertEquals(user1, mapper.getUserById(1));
        assertEquals(user2, mapper.getUserById(2));
        assertEquals(user3, mapper.getUserById(3));

        ListElementRequest ler = ListElementRequest.builder().offset(0).limit(10).build();
        assertEquals(Lists.newArrayList(user1, user2, user3), mapper.listUsers(ler));
        assertEquals(3, mapper.countUsers(ler));

        ler.setOffset(1);
        ler.setLimit(1);
        assertEquals(Lists.newArrayList(user2), mapper.listUsers(ler));
        assertEquals(3, mapper.countUsers(ler));

        ler.setOffset(0);
        ler.setFilters(Lists.newArrayList(Filter.gt("id", 1)));
        assertEquals(Lists.newArrayList(user2), mapper.listUsers(ler));
        assertEquals(2, mapper.countUsers(ler));
    }

    @Test
    void testUserCreate() {
        User user = createUser();
        assertEquals(1, mapper.insertUser(user));
        assertTrue(user.getId() > 0);
        assertEquals(user, mapper.getUser(user.getUserId()));
    }

    @Test
    void testUserDelete() {
        assertEquals(3, mapper.deleteUsers(Lists.newArrayList("uId1", "uId2", "uId3")));
        assertNull(mapper.getUser("uId1"));
        assertNull(mapper.getUser("uId2"));
        assertNull(mapper.getUser("uId3"));
    }

    @Test
    void testRoleRead() {
        Role role1 = mapper.getRole(1), role2 = mapper.getRole(2);
        validateRole(role1, 1, "admin", "administrator", DATE, DATE);
        validateRole(role2, 2, "analyst", "read only", DATE, DATE);

        ListElementRequest ler = ListElementRequest.builder().offset(0).limit(10).build();
        assertEquals(Lists.newArrayList(role1, role2), mapper.listRoles(ler));
        assertEquals(2, mapper.countRoles(ler));

        ler.setOffset(1);
        ler.setLimit(1);
        assertEquals(Lists.newArrayList(role2), mapper.listRoles(ler));
        assertEquals(2, mapper.countRoles(ler));

        ler.setOffset(0);
        ler.setFilters(Lists.newArrayList(Filter.gt("id", 1)));
        assertEquals(Lists.newArrayList(role2), mapper.listRoles(ler));
        assertEquals(1, mapper.countRoles(ler));
    }

    @Test
    void testRoleCreate() {
        Role role = createRole();
        assertEquals(1, mapper.insertRole(role));
        assertTrue(role.getId() > 0);
        assertEquals(role, mapper.getRole(role.getId()));
    }

    @Test
    void testRoleUpdate() {
        Role role = mapper.getRole(1);
        role.setDescription("new description");
        role.setUpdatedAt(new Date());
        assertEquals(1, mapper.updateRole(role));
        assertEquals(role, mapper.getRole(1));
    }

    @Test
    void testRoleDelete() {
        assertEquals(2, mapper.deleteRoles(Lists.newArrayList(1L, 2L)));
        assertNull(mapper.getRole(1));
        assertNull(mapper.getRole(2));
    }

    @Test
    void testPolicyRead() {
        Policy policy1 = mapper.getPolicy(1), policy2 = mapper.getPolicy(2);
        Policy policy3 = mapper.getPolicy(3), policy4 = mapper.getPolicy(4);
        Resource r1 = new Resource(ResourceType.HDFS, "hdfs://host:8020/data");
        Resource r2 = new Resource(ResourceType.TAG, "tag1");
        validatePolicy(policy1, 1, "hdfs_reader", "hdfs read only role", r1, Action.Read, DATE, DATE);
        validatePolicy(policy2, 2, "hdfs_writer", "hdfs write role", r1, Action.Write, DATE, DATE);
        validatePolicy(policy3, 3, "tag_reader", "tag read only role", r2, Action.Read, DATE, DATE);
        validatePolicy(policy4, 4, "tag_writer", "tag write role", r2, Action.Write, DATE, DATE);

        ListElementRequest ler = ListElementRequest.builder().offset(0).limit(10).build();
        assertEquals(Lists.newArrayList(policy1, policy2, policy3, policy4), mapper.listPolicies(ler));
        assertEquals(4, mapper.countPolicies(ler));

        ler.setOffset(1);
        ler.setLimit(1);
        assertEquals(Lists.newArrayList(policy2), mapper.listPolicies(ler));
        assertEquals(4, mapper.countPolicies(ler));

        ler.setOffset(0);
        ler.setFilters(Lists.newArrayList(Filter.gt("id", 1)));
        assertEquals(Lists.newArrayList(policy2), mapper.listPolicies(ler));
        assertEquals(3, mapper.countPolicies(ler));
    }

    @Test
    void testPolicyCreate() {
        Policy policy = createPolicy();
        assertEquals(1, mapper.insertPolicy(policy));
        assertTrue(policy.getId() > 0);
        assertEquals(policy, mapper.getPolicy(policy.getId()));
    }

    @Test
    void testPolicyUpdate() {
        Policy policy = mapper.getPolicy(1);
        policy.setDescription("new description");
        policy.setUpdatedAt(new Date());
        assertEquals(1, mapper.updatePolicy(policy));
        assertEquals(policy, mapper.getPolicy(1));
    }

    @Test
    void testPolicyDelete() {
        assertEquals(4, mapper.deletePolicies(Lists.newArrayList(1L, 2L, 3L, 4L)));
        assertNull(mapper.getPolicy(1));
        assertNull(mapper.getPolicy(2));
        assertNull(mapper.getPolicy(3));
        assertNull(mapper.getPolicy(4));
    }

    @Test
    void testGetResourcesPolicies() {
        Policy policy1 = mapper.getPolicy(1), policy2 = mapper.getPolicy(2);
        Policy policy3 = mapper.getPolicy(3), policy4 = mapper.getPolicy(4);
        Resource r1 = new Resource(ResourceType.HDFS, "hdfs://host:8020/data");
        Resource r2 = new Resource(ResourceType.TAG, "tag1");

        assertEquals(Lists.newArrayList(policy1, policy2, policy3, policy4),
                mapper.getResourcesPolicies(Lists.newArrayList(r1, r2)));
        assertEquals(Lists.newArrayList(policy1, policy2), mapper.getResourcesPolicies(Lists.newArrayList(r1)));
        assertEquals(Lists.newArrayList(policy3, policy4), mapper.getResourcesPolicies(Lists.newArrayList(r2)));
    }

    @Test
    void testUserRoleRead() {
        UserRole ur1 = new UserRole(1, 1, DATE), ur2 = new UserRole(2, 2, DATE);
        User user1 = mapper.getUser("uId1"), user2 = mapper.getUser("uId2");
        Role role1 = mapper.getRole(1), role2 = mapper.getRole(2);

        assertEquals(ur1, mapper.getUserRole(1, 1));
        assertEquals(ur2, mapper.getUserRole(2, 2));

        assertEquals(Lists.newArrayList(user1), mapper.listRoleUsers(1, 0, 10));
        assertEquals(1, mapper.countRoleUsers(1));
        assertEquals(Lists.newArrayList(user2), mapper.listRoleUsers(2, 0, 10));
        assertEquals(1, mapper.countRoleUsers(2));

        assertEquals(Lists.newArrayList(role1), mapper.listUserRoles(1, 0, 10));
        assertEquals(1, mapper.countUserRoles(1));
        assertEquals(Lists.newArrayList(role2), mapper.listUserRoles(2, 0, 10));
        assertEquals(1, mapper.countUserRoles(2));
        assertEquals(Lists.newArrayList(), mapper.listUserRoles(3, 0, 10));
        assertEquals(0, mapper.countUserRoles(3));
    }

    @Test
    void testUserRoleCreate() {
        assertNull(mapper.getUserRole(3, 1));
        assertEquals(1, mapper.insertUserRole(3, 1));
        assertNotNull(mapper.getUserRole(3, 1));
    }

    @Test
    void testUserRoleDelete() {
        assertNotNull(mapper.getUserRole(1, 1));
        assertEquals(1, mapper.deleteUserRole(1, 1));
        assertNull(mapper.getUserRole(1, 1));
    }

    @Test
    void testUsersRolesDelete() {
        assertNotNull(mapper.getUserRole(1, 1));
        assertEquals(1, mapper.deleteUserRoles(1));
        assertNull(mapper.getUserRole(1, 1));

        assertNotNull(mapper.getUserRole(2, 2));
        assertEquals(1, mapper.deleteRoleUsers(2));
        assertNull(mapper.getUserRole(2, 2));
    }

    @Test
    void testGetUserPolicy() {
        assertEquals(new EntityPolicy(EntityType.Role, 1, 2, DATE), mapper.getUserPolicy(1, Lists.newArrayList(1L, 2L)));
        assertEquals(new EntityPolicy(EntityType.Role, 2, 3, DATE), mapper.getUserPolicy(2, Lists.newArrayList(3L , 4L)));
        assertEquals(new EntityPolicy(EntityType.User, 3, 4, DATE), mapper.getUserPolicy(3, Lists.newArrayList(4L)));
        assertNull(mapper.getUserPolicy(2, Lists.newArrayList(2L)));
    }

    @Test
    void testEntityPolicyRead() {
        EntityPolicy ep1 = new EntityPolicy(EntityType.Role, 1, 2, DATE), ep2 = new EntityPolicy(EntityType.Role, 1, 4, DATE);
        EntityPolicy ep3 = new EntityPolicy(EntityType.Role, 2, 1, DATE), ep4 = new EntityPolicy(EntityType.Role, 2, 3, DATE);
        EntityPolicy ep5 = new EntityPolicy(EntityType.User, 3, 1, DATE), ep6 = new EntityPolicy(EntityType.User, 3, 4, DATE);
        Entity e1 = new Entity(EntityType.Role, 1), e2 = new Entity(EntityType.Role, 2), e3 = new Entity(EntityType.User, 3);
        Policy policy1 = mapper.getPolicy(1), policy2 = mapper.getPolicy(2);
        Policy policy3 = mapper.getPolicy(3), policy4 = mapper.getPolicy(4);

        assertEquals(ep1, mapper.getEntityPolicy(EntityType.Role, 1, 2));
        assertEquals(ep2, mapper.getEntityPolicy(EntityType.Role, 1, 4));
        assertEquals(ep3, mapper.getEntityPolicy(EntityType.Role, 2, 1));
        assertEquals(ep4, mapper.getEntityPolicy(EntityType.Role, 2, 3));
        assertEquals(ep5, mapper.getEntityPolicy(EntityType.User, 3, 1));
        assertEquals(ep6, mapper.getEntityPolicy(EntityType.User, 3, 4));

        assertEquals(Lists.newArrayList(e2, e3), mapper.listPolicyEntities(1, 0, 10));
        assertEquals(2, mapper.countPolicyEntities(1));
        assertEquals(Lists.newArrayList(e1), mapper.listPolicyEntities(2, 0, 10));
        assertEquals(1, mapper.countPolicyEntities(2));
        assertEquals(Lists.newArrayList(e2), mapper.listPolicyEntities(3, 0, 10));
        assertEquals(1, mapper.countPolicyEntities(3));
        assertEquals(Lists.newArrayList(e1, e3), mapper.listPolicyEntities(4, 0, 10));
        assertEquals(2, mapper.countPolicyEntities(4));

        assertEquals(Lists.newArrayList(policy2, policy4), mapper.listEntityPolicies(e1, 0, 10));
        assertEquals(2, mapper.countEntityPolicies(e1));
        assertEquals(Lists.newArrayList(policy1, policy3), mapper.listEntityPolicies(e2, 0, 10));
        assertEquals(2, mapper.countEntityPolicies(e2));
        assertEquals(Lists.newArrayList(policy1, policy4), mapper.listEntityPolicies(e3, 0, 10));
        assertEquals(2, mapper.countEntityPolicies(e3));
    }

    @Test
    void testEntityPolicyCreate() {
        assertNull(mapper.getEntityPolicy(EntityType.Role, 1, 1));
        assertEquals(1, mapper.insertEntityPolicy(EntityType.Role, 1, 1));
        assertNotNull(mapper.getEntityPolicy(EntityType.Role, 1, 1));
    }

    @Test
    void testEntityPolicyDelete() {
        assertNotNull(mapper.getEntityPolicy(EntityType.Role, 1, 2));
        assertEquals(1, mapper.deleteEntityPolicy(EntityType.Role, 1, 2));
        assertNull(mapper.getEntityPolicy(EntityType.Role, 1, 2));
    }

    @Test
    void testEntitiesPoliciesDelete() {
        Entity entity = new Entity(EntityType.Role, 1);
        assertEquals(2, mapper.countEntityPolicies(entity));
        assertEquals(2, mapper.deleteEntityPolicies(entity));
        assertEquals(0, mapper.countEntityPolicies(entity));

        assertEquals(2, mapper.countPolicyEntities(1));
        assertEquals(2, mapper.deletePolicyEntities(1));
        assertEquals(0, mapper.countPolicyEntities(1));
    }

    private User createUser() {
        User user = new User();
        user.setUserId(randomAlphanumeric(10));
        user.setUsername(randomAlphanumeric(10));
        user.setAdmin(true);
        user.setCreatedAt(new Date());
        return user;
    }

    private void validateUser(User user, long id, String userId, String username, boolean isAdmin, Date createdAt) {
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(user.getType(), EntityType.User);
        assertEquals(userId, user.getUserId());
        assertEquals(username, user.getUsername());
        assertEquals(isAdmin, user.isAdmin());
        assertEquals(createdAt, user.getCreatedAt());
    }

    private Role createRole() {
        Role role = new Role();
        Date now = new Date();
        role.setName(randomAlphanumeric(16));
        role.setDescription(randomAlphanumeric(192));
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        return role;
    }

    private void validateRole(Role role, long id, String name, String description, Date createdAt, Date updatedAt) {
        assertNotNull(role);
        assertEquals(id, role.getId());
        assertEquals(EntityType.Role, role.getType());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
        assertEquals(createdAt, role.getCreatedAt());
        assertEquals(updatedAt, role.getUpdatedAt());
    }

    private Policy createPolicy() {
        Policy policy = new Policy();
        Date now = new Date();
        policy.setName(randomAlphanumeric(16));
        policy.setDescription(randomAlphanumeric(192));
        policy.setAction(Action.Read);
        policy.setResource(new Resource(ResourceType.TAG, randomAlphanumeric(10)));
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);
        return policy;
    }

    private void validatePolicy(Policy policy, long id, String name, String description, Resource resource, Action action,
                                Date createdAt, Date updatedAt) {
        assertNotNull(policy);
        assertEquals(id, policy.getId());
        assertEquals(name, policy.getName());
        assertEquals(description, policy.getDescription());
        assertEquals(resource, policy.getResource());
        assertEquals(action, policy.getAction());
        assertEquals(createdAt, policy.getCreatedAt());
        assertEquals(updatedAt, policy.getUpdatedAt());
    }
}
