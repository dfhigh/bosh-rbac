package com.bosh.rbac.mapper;

import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.model.EntityPolicy;
import com.bosh.rbac.model.User;
import com.bosh.rbac.model.UserRole;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mib.metrics.Metrics;
import org.mib.metrics.MetricsScope;
import org.mib.rest.model.list.ListElementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mib.common.validator.Validator.validateCollectionNotEmptyContainsNoNull;
import static org.mib.common.validator.Validator.validateLongNotNegative;
import static org.mib.common.validator.Validator.validateLongPositive;
import static org.mib.common.validator.Validator.validateObjectNotNull;
import static org.mib.common.validator.Validator.validateStringNotBlank;

@Mapper
@Repository
public interface RbacMapper {

    Logger log = LoggerFactory.getLogger(RbacMapper.class);

    default int insertUser(User user) {
        validateObjectNotNull(user, "user");
        return insertUsers(Lists.newArrayList(user));
    }

    int _insertUsers(@Param("list") Collection<User> users);

    default int insertUsers(Collection<User> users) {
        validateCollectionNotEmptyContainsNoNull(users, "user collection");
        users.forEach(user -> {
            validateStringNotBlank(user.getUserId(), "user id");
            validateStringNotBlank(user.getUsername(), "username");
        });
        log.debug("inserting users {}...", users);
        long start = System.currentTimeMillis();
        int inserted = _insertUsers(users);
        long end = System.currentTimeMillis();
        log.debug("inserted {} users {}", inserted, users);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("insertUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("insertUsers", 1);
        metrics.addMetric("insertUsers.inserted", users.size());
        return inserted;
    }

    User _getUser(@Param("userId") String userId);

    default User getUser(String userId) {
        validateStringNotBlank(userId, "user id");
        log.debug("retrieving user {}...", userId);
        long start = System.currentTimeMillis();
        User user = _getUser(userId);
        long end = System.currentTimeMillis();
        log.debug("retrieved user {}", user);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getUser", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getUser", 1);
        return user;
    }

    User _getUserById(@Param("id") long id);

    default User getUserById(long id) {
        validateLongPositive(id, "user id");
        log.debug("retrieving user by pk {}...", id);
        long start = System.currentTimeMillis();
        User user = _getUserById(id);
        long end = System.currentTimeMillis();
        log.debug("retrieved user {}", user);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getUserById", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getUserById", 1);
        return user;
    }

    List<User> _listUsers(@Param("ler") ListElementRequest ler);

    default List<User> listUsers(ListElementRequest ler) {
        validateObjectNotNull(ler, "listing request");
        validateLongNotNegative(ler.getOffset(), "listing offset");
        validateLongNotNegative(ler.getLimit(), "listing limit");
        log.debug("listing users with params {}...", ler);
        long start = System.currentTimeMillis();
        List<User> users = _listUsers(ler);
        long end = System.currentTimeMillis();
        log.debug("listed users {} with params {}", users, ler);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listUsers", 1);
        metrics.addMetric("listUsers.listed", users.size());
        return users;
    }

    long _countUsers(@Param("ler") ListElementRequest ler);

    default long countUsers(ListElementRequest ler) {
        validateObjectNotNull(ler, "listing params");
        log.debug("counting users with params {}...", ler);
        long start = System.currentTimeMillis();
        long result = _countUsers(ler);
        long end = System.currentTimeMillis();
        log.debug("counted {} users with listing params {}", result, ler);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countUsers", 1);
        return result;
    }

    int _deleteUsers(@Param("userIds") Collection<String> userIds);

    default int deleteUsers(Collection<String> userIds) {
        validateCollectionNotEmptyContainsNoNull(userIds, "user id collection");
        userIds.forEach(userId -> validateStringNotBlank(userId, "user id"));
        log.debug("deleting users {}...", userIds);
        long start = System.currentTimeMillis();
        int deleted = _deleteUsers(userIds);
        long end = System.currentTimeMillis();
        log.debug("deleted {} users", deleted);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteUsers", 1);
        metrics.addMetric("deleteUsers.deleted", deleted);
        return deleted;
    }

    int _insertRole(@Param("role") Role role);

    default int insertRole(Role role) {
        validateObjectNotNull(role, "role");
        validateStringNotBlank(role.getName(), "role name");
        log.debug("inserting role {}...", role);
        long start = System.currentTimeMillis();
        int inserted = _insertRole(role);
        long end = System.currentTimeMillis();
        log.debug("inserted {} role {}", inserted, role);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("insertRole", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("insertRole", 1);
        metrics.addMetric("insertRole.inserted", inserted);
        return inserted;
    }

    Role _getRole(@Param("id") long id);

    default Role getRole(long roleId) {
        validateLongPositive(roleId, "role id");
        log.debug("retrieving role {}...", roleId);
        long start = System.currentTimeMillis();
        Role role = _getRole(roleId);
        long end = System.currentTimeMillis();
        log.debug("retrieved role {}", role);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getRole", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getRole", 1);
        return role;
    }

    List<Role> _listRoles(@Param("ler") ListElementRequest ler);

    default List<Role> listRoles(ListElementRequest ler) {
        validateObjectNotNull(ler, "listing params");
        validateLongNotNegative(ler.getOffset(), "listing offset");
        validateLongNotNegative(ler.getLimit(), "listing limit");
        log.debug("listing roles with params {}...", ler);
        long start = System.currentTimeMillis();
        List<Role> roles = _listRoles(ler);
        long end = System.currentTimeMillis();
        log.debug("listed roles {} with params {}", roles, ler);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listRoles", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listRoles", 1);
        metrics.addMetric("listRoles.listed", roles.size());
        return roles;
    }

    long _countRoles(@Param("ler") ListElementRequest ler);

    default long countRoles(ListElementRequest ler) {
        validateObjectNotNull(ler, "listing params");
        log.debug("counting roles with params {}...", ler);
        long start = System.currentTimeMillis();
        long result = _countRoles(ler);
        long end = System.currentTimeMillis();
        log.debug("counted {} roles with listing params {}", result, ler);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countRoles", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countRoles", 1);
        return result;
    }

    int _updateRole(@Param("role") Role role);

    default int updateRole(Role role) {
        validateObjectNotNull(role, "role");
        validateLongPositive(role.getId(), "role id");
        log.debug("updating role {}...", role);
        long start = System.currentTimeMillis();
        int updated = _updateRole(role);
        long end = System.currentTimeMillis();
        log.debug("updated role {}", role.getId());
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("updateRole", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("updateRole", 1);
        metrics.addMetric("updateRole.updated", updated);
        return updated;
    }

    int _deleteRoles(@Param("roleIds") Collection<Long> roleIds);

    default int deleteRoles(Collection<Long> roleIds) {
        validateCollectionNotEmptyContainsNoNull(roleIds, "role id collection");
        roleIds.forEach(roleId -> validateLongPositive(roleId, "role id"));
        log.debug("deleting roles {}...", roleIds);
        long start = System.currentTimeMillis();
        int deleted = _deleteRoles(roleIds);
        long end = System.currentTimeMillis();
        log.debug("deleted {} roles", deleted);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteRoles", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteRoles", 1);
        metrics.addMetric("deleteRoles.deleted", deleted);
        return deleted;
    }

    int _insertPolicy(@Param("policy") Policy policy);

    default int insertPolicy(Policy policy) {
        validateObjectNotNull(policy, "policy");
        validateStringNotBlank(policy.getName(), "policy name");
        validateObjectNotNull(policy.getResource(), "policy resource");
        validateObjectNotNull(policy.getResource().getType(), "policy resource type");
        validateStringNotBlank(policy.getResource().getValue(), "policy resource value");
        validateObjectNotNull(policy.getAction(), "policy action");
        log.debug("inserting policy {}...", policy);
        long start = System.currentTimeMillis();
        int inserted = _insertPolicy(policy);
        long end = System.currentTimeMillis();
        log.debug("inserted {} policy {}", inserted, policy);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("insertPolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("insertPolicy", 1);
        metrics.addMetric("insertPolicy.inserted", inserted);
        return inserted;
    }

    Policy _getPolicy(@Param("id") long id);

    default Policy getPolicy(long policyId) {
        validateLongPositive(policyId, "policy id");
        log.debug("retrieving policy {}...", policyId);
        long start = System.currentTimeMillis();
        Policy policy = _getPolicy(policyId);
        long end = System.currentTimeMillis();
        log.debug("retrieved policy {}", policy);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getPolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getPolicy", 1);
        return policy;
    }

    List<Policy> _listPolicies(@Param("ler") ListElementRequest ler);

    default List<Policy> listPolicies(ListElementRequest ler) {
        validateObjectNotNull(ler, "listing params");
        validateLongNotNegative(ler.getOffset(), "listing offset");
        validateLongNotNegative(ler.getLimit(), "listing limit");
        log.debug("listing policies with params {}...", ler);
        long start = System.currentTimeMillis();
        List<Policy> policies = _listPolicies(ler);
        long end = System.currentTimeMillis();
        log.debug("listed policies {} with params {}", policies, ler);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listPolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listPolicies", 1);
        metrics.addMetric("listPolicies.listed", policies.size());
        return policies;
    }

    long _countPolicies(@Param("ler") ListElementRequest ler);

    default long countPolicies(ListElementRequest ler) {
        validateObjectNotNull(ler, "listing params");
        log.debug("counting policies with params {}...", ler);
        long start = System.currentTimeMillis();
        long result = _countPolicies(ler);
        long end = System.currentTimeMillis();
        log.debug("counted {} policies with listing params {}", result, ler);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countPolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countPolicies", 1);
        return result;
    }

    List<Policy> _getResourcesPolicies(@Param("resources") Collection<Resource> resources);

    default List<Policy> getResourcesPolicies(Collection<Resource> resources) {
        validateCollectionNotEmptyContainsNoNull(resources, "resource collection");
        resources.forEach(resource -> {
            validateObjectNotNull(resource.getType(), "resource type");
            validateStringNotBlank(resource.getValue(), "resource value");
        });
        log.debug("retrieving policies for resources {}...", resources);
        long start = System.currentTimeMillis();
        List<Policy> policies = _getResourcesPolicies(resources);
        long end = System.currentTimeMillis();
        log.debug("retrieved policies {}", policies);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getResourcesPolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getResourcesPolicies", 1);
        metrics.addMetric("getResourcesPolicies.got", policies.size());
        return policies;
    }

    int _updatePolicy(@Param("policy") Policy policy);

    default int updatePolicy(Policy policy) {
        validateObjectNotNull(policy, "policy");
        validateLongPositive(policy.getId(), "policy id");
        log.debug("updating policy {}...", policy);
        long start = System.currentTimeMillis();
        int updated = _updatePolicy(policy);
        long end = System.currentTimeMillis();
        log.debug("updated policy {}", policy.getId());
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("updatePolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("updatePolicy", 1);
        metrics.addMetric("updatePolicy.updated", updated);
        return updated;
    }

    int _deletePolicies(@Param("policyIds") Collection<Long> policyIds);

    default int deletePolicies(Collection<Long> policyIds) {
        validateCollectionNotEmptyContainsNoNull(policyIds, "policy id collection");
        policyIds.forEach(roleId -> validateLongPositive(roleId, "policy id"));
        log.debug("deleting policies {}...", policyIds);
        long start = System.currentTimeMillis();
        int deleted = _deletePolicies(policyIds);
        long end = System.currentTimeMillis();
        log.debug("deleted {} policies", deleted);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deletePolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deletePolicies", 1);
        metrics.addMetric("deletePolicies.deleted", deleted);
        return deleted;
    }

    int _insertUserRole(@Param("userId") long userId, @Param("roleId") long roleId, @Param("createdAt") Date createdAt);

    default int insertUserRole(long userId, long roleId) {
        validateLongPositive(userId, "user id");
        validateLongPositive(roleId, "role id");
        log.debug("assigning user {} to role {}...", userId, roleId);
        long start = System.currentTimeMillis();
        int inserted = _insertUserRole(userId, roleId, new Date());
        long end = System.currentTimeMillis();
        log.debug("assigned user {} to role {}", userId, roleId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("insertUserRole", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("insertUserRole", 1);
        return inserted;
    }

    UserRole _getUserRole(@Param("userId") long userId, @Param("roleId") long roleId);

    default UserRole getUserRole(long userId, long roleId) {
        validateLongPositive(userId, "user id");
        validateLongPositive(roleId, "role id");
        log.debug("retrieving user {} role {}...", userId, roleId);
        long start = System.currentTimeMillis();
        UserRole ur = _getUserRole(userId, roleId);
        long end = System.currentTimeMillis();
        log.debug("retrieved user role {}", ur);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getUserRole", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getUserRole", 1);
        return ur;
    }

    List<User> _listRoleUsers(@Param("roleId") long roleId, @Param("offset") long offset, @Param("limit") long limit);

    default List<User> listRoleUsers(long roleId, long offset, long limit) {
        validateLongPositive(roleId, "role id");
        validateLongNotNegative(offset, "listing offset");
        validateLongPositive(limit, "listing limit");
        log.debug("listing users of role {} with offset {} and limit {}...", roleId, offset, limit);
        long start = System.currentTimeMillis();
        List<User> users = _listRoleUsers(roleId, offset, limit);
        long end = System.currentTimeMillis();
        log.debug("listed users {}", users);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listRoleUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listRoleUsers", 1);
        metrics.addMetric("listRoleUsers.listed", users.size());
        return users;
    }

    long _countRoleUsers(@Param("roleId") long roleId);

    default long countRoleUsers(long roleId) {
        validateLongPositive(roleId, "role id");
        log.debug("counting users assigned with role {}...", roleId);
        long start = System.currentTimeMillis();
        long count = _countRoleUsers(roleId);
        long end = System.currentTimeMillis();
        log.debug("counted {} users for role {}", count, roleId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countRoleUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countRoleUsers", 1);
        metrics.addMetric("countRoleUsers.count", count);
        return count;
    }

    List<Role> _listUserRoles(@Param("userId") long userId, @Param("offset") long offset, @Param("limit") long limit);

    default List<Role> listUserRoles(long userId, long offset, long limit) {
        validateLongPositive(userId, "user id");
        validateLongNotNegative(offset, "listing offset");
        validateLongPositive(limit, "listing limit");
        log.debug("listing roles for user {} with offset {} and limit {}...", userId, offset, limit);
        long start = System.currentTimeMillis();
        List<Role> roles = _listUserRoles(userId, offset, limit);
        long end = System.currentTimeMillis();
        log.debug("listed roles {}", roles);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listUserRoles", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listUserRoles", 1);
        metrics.addMetric("listUserRoles.listed", roles.size());
        return roles;
    }

    long _countUserRoles(@Param("userId") long userId);

    default long countUserRoles(long userId) {
        validateLongPositive(userId, "user id");
        log.debug("counting roles assigned to user {}...", userId);
        long start = System.currentTimeMillis();
        long count = _countUserRoles(userId);
        long end = System.currentTimeMillis();
        log.debug("counted {} roles for user {}", count, userId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countUserRoles", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countUserRoles", 1);
        metrics.addMetric("countUserRoles.count", count);
        return count;
    }

    int _deleteUserRole(@Param("userId") long userId, @Param("roleId") long roleId);

    default int deleteUserRole(long userId, long roleId) {
        validateLongPositive(userId, "user id");
        validateLongPositive(roleId, "role id");
        log.debug("deleting user {} role {}...", userId, roleId);
        long start = System.currentTimeMillis();
        int deleted = _deleteUserRole(userId, roleId);
        long end = System.currentTimeMillis();
        log.debug("deleted {} user role", deleted);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteUserRole", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteUserRole", 1);
        metrics.addMetric("deleteUserRole.deleted", deleted);
        return deleted;
    }

    int _deleteUserRoles(@Param("userId") long userId);

    default int deleteUserRoles(long userId) {
        validateLongPositive(userId, "user id");
        log.debug("deleting user {} roles...", userId);
        long start = System.currentTimeMillis();
        int deleted = _deleteUserRoles(userId);
        long end = System.currentTimeMillis();
        log.debug("deleted {} roles for user {}", deleted, userId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteUserRoles", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteUserRoles", 1);
        metrics.addMetric("deleteUserRoles.deleted", deleted);
        return deleted;
    }

    int _deleteRoleUsers(@Param("roleId") long roleId);

    default int deleteRoleUsers(long roleId) {
        validateLongPositive(roleId, "role id");
        log.debug("deleting role {} users...", roleId);
        long start = System.currentTimeMillis();
        int deleted = _deleteRoleUsers(roleId);
        long end = System.currentTimeMillis();
        log.debug("deleted {} users for role {}", deleted, roleId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteRoleUsers", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteRoleUsers", 1);
        metrics.addMetric("deleteRoleUsers.deleted", deleted);
        return deleted;
    }

    int _insertEntityPolicy(@Param("entityType") int entityType,
                            @Param("entityId") long entityId,
                            @Param("policyId") long policyId,
                            @Param("createdAt") Date createdAt);

    default int insertEntityPolicy(EntityType entityType, long entityId, long policyId) {
        validateObjectNotNull(entityType, "entity type");
        validateLongPositive(entityId, "entity id");
        validateLongPositive(policyId, "policy id");
        log.debug("assigning policy {} to {} {}...", policyId, entityType.name(), entityId);
        long start = System.currentTimeMillis();
        int inserted = _insertEntityPolicy(entityType.getValue(), entityId, policyId, new Date());
        long end = System.currentTimeMillis();
        log.debug("assigned policy {} to {} {}", policyId, entityType.name(), entityId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("insertEntityPolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("insertEntityPolicy", 1);
        return inserted;
    }

    EntityPolicy _getEntityPolicy(@Param("entityType") int entityType, @Param("entityId") long entityId, @Param("policyId") long policyId);

    default EntityPolicy getEntityPolicy(EntityType entityType, long entityId, long policyId) {
        validateObjectNotNull(entityType, "entity type");
        validateLongPositive(entityId, "entity id");
        validateLongPositive(policyId, "policy id");
        log.debug("retrieving {} {} policy {}...", entityType.name(), entityId, policyId);
        long start = System.currentTimeMillis();
        EntityPolicy ep = _getEntityPolicy(entityType.getValue(), entityId, policyId);
        long end = System.currentTimeMillis();
        log.debug("retrieved {}", ep);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getEntityPolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getEntityPolicy", 1);
        return ep;
    }

    List<Entity> _listPolicyEntities(@Param("policyId") long policyId,
                                     @Param("offset") long offset,
                                     @Param("limit") long limit);

    default List<Entity> listPolicyEntities(long policyId, long offset, long limit) {
        validateLongPositive(policyId, "policy id");
        validateLongNotNegative(offset, "listing offset");
        validateLongPositive(limit, "listing limit");
        log.debug("listing entities for policy {} with offset {} and limit {}...", policyId, offset, limit);
        long start = System.currentTimeMillis();
        List<Entity> entities = _listPolicyEntities(policyId, offset, limit);
        long end = System.currentTimeMillis();
        log.debug("listed entities {}", entities);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listPolicyEntities", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listPolicyEntities", 1);
        metrics.addMetric("listPolicyEntities.listed", entities.size());
        return entities;
    }

    long _countPolicyEntities(@Param("policyId") long policyId);

    default long countPolicyEntities(long policyId) {
        validateLongPositive(policyId, "policy id");
        log.debug("counting entities for policy {}...", policyId);
        long start = System.currentTimeMillis();
        long count = _countPolicyEntities(policyId);
        long end = System.currentTimeMillis();
        log.debug("counted {} entities for policy {}", count, policyId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countPolicyEntities", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countPolicyEntities", 1);
        metrics.addMetric("countPolicyEntities.count", count);
        return count;
    }

    List<Policy> _listEntityPolicies(@Param("entityType") int entityType,
                                     @Param("entityId") long entityId,
                                     @Param("offset") long offset,
                                     @Param("limit") long limit);

    default List<Policy> listEntityPolicies(Entity entity, long offset, long limit) {
        validateObjectNotNull(entity, "entity");
        validateObjectNotNull(entity.getType(), "entity type");
        validateLongPositive(entity.getId(), "entity id");
        validateLongNotNegative(offset, "listing offset");
        validateLongPositive(limit, "listing limit");
        log.debug("listing policies for entity {} with offset {} and limit {}...", entity, offset, limit);
        long start = System.currentTimeMillis();
        List<Policy> policies = _listEntityPolicies(entity.getType().getValue(), entity.getId(), offset, limit);
        long end = System.currentTimeMillis();
        log.debug("listed policies {}", policies);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("listEntityPolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("listEntityPolicies", 1);
        metrics.addMetric("listEntityPolicies.listed", policies.size());
        return policies;
    }

    long _countEntityPolicies(@Param("entityType") int entityType, @Param("entityId") long entityId);

    default long countEntityPolicies(Entity entity) {
        validateObjectNotNull(entity, "entity");
        validateObjectNotNull(entity.getType(), "entity type");
        validateLongPositive(entity.getId(), "entity id");
        log.debug("counting policies for entity {}...", entity);
        long start = System.currentTimeMillis();
        long count = _countEntityPolicies(entity.getType().getValue(), entity.getId());
        long end = System.currentTimeMillis();
        log.debug("counted {} policies for entity {}", count, entity);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("countEntityPolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("countEntityPolicies", 1);
        metrics.addMetric("countEntityPolicies.count", count);
        return count;
    }

    EntityPolicy _getUserPolicy(@Param("userId") long userId, @Param("policyIds") Collection<Long> policyIds);

    default EntityPolicy getUserPolicy(long userId, Collection<Long> policyIds) {
        validateLongPositive(userId, "user id");
        validateCollectionNotEmptyContainsNoNull(policyIds, "policy ids");
        policyIds.forEach(policyId -> validateLongPositive(policyId, "policy id"));
        log.debug("retrieving policy for user {} inside policies {}...", userId, policyIds);
        long start = System.currentTimeMillis();
        EntityPolicy ep = _getUserPolicy(userId, policyIds);
        long end = System.currentTimeMillis();
        log.debug("retrieved {}", ep);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("getUserPolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("getUserPolicy", 1);
        return ep;
    }

    int _deleteEntityPolicy(@Param("entityType") int entityType, @Param("entityId") long entityId, @Param("policyId") long policyId);

    default int deleteEntityPolicy(EntityType entityType, long entityId, long policyId) {
        validateObjectNotNull(entityType, "entity type");
        validateLongPositive(entityId, "entity id");
        validateLongPositive(policyId, "policy id");
        log.debug("removing policy {} from {} {}...", policyId, entityType.name(), entityId);
        long start = System.currentTimeMillis();
        int deleted = _deleteEntityPolicy(entityType.getValue(), entityId, policyId);
        long end = System.currentTimeMillis();
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteEntityPolicy", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteEntityPolicy", 1);
        metrics.addMetric("deleteEntityPolicy.deleted", 1);
        return deleted;
    }

    int _deleteEntityPolicies(@Param("entityType") int entityType, @Param("entityId") long entityId);

    default int deleteEntityPolicies(Entity entity) {
        validateObjectNotNull(entity, "entity");
        validateObjectNotNull(entity.getType(), "entity type");
        validateLongPositive(entity.getId(), "entity id");
        log.debug("deleting entity {} policies...", entity);
        long start = System.currentTimeMillis();
        int deleted = _deleteEntityPolicies(entity.getType().getValue(), entity.getId());
        long end = System.currentTimeMillis();
        log.debug("deleted {} policies for entity {}", deleted, entity);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deleteEntityPolicies", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deleteEntityPolicies", 1);
        metrics.addMetric("deleteEntityPolicies.deleted", deleted);
        return deleted;
    }

    int _deletePolicyEntities(@Param("policyId") long policyId);

    default int deletePolicyEntities(long policyId) {
        validateLongPositive(policyId, "policy id");
        log.debug("deleting policy {} entities...", policyId);
        long start = System.currentTimeMillis();
        int deleted = _deletePolicyEntities(policyId);
        long end = System.currentTimeMillis();
        log.debug("deleted {} entities for policy {}", deleted, policyId);
        Metrics metrics = MetricsScope.getMetrics();
        metrics.addTime("deletePolicyEntities", start, end, TimeUnit.MILLISECONDS);
        metrics.addCounter("deletePolicyEntities", 1);
        metrics.addMetric("deletePolicyEntities.deleted", deleted);
        return deleted;
    }
}
