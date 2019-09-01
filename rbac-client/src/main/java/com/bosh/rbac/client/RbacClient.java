package com.bosh.rbac.client;

import com.bosh.rbac.auth.model.AuthRequest;
import com.bosh.rbac.auth.model.AuthResponse;
import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.model.User;
import com.bosh.rbac.rest.model.DescriptionUpdate;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.mib.rest.client.HttpExecution;
import org.mib.rest.client.HttpOperator;
import org.mib.rest.client.SyncHttpOperator;
import org.mib.rest.model.list.ListPayload;

import static org.mib.common.validator.Validator.validateLongNotNegative;
import static org.mib.common.validator.Validator.validateLongPositive;
import static org.mib.common.validator.Validator.validateObjectNotNull;
import static org.mib.common.validator.Validator.validateStringNotBlank;

@Slf4j
public class RbacClient implements RbacAuthorizer {

    private static final TypeReference<ListPayload<User>> USERS_TR = new TypeReference<ListPayload<User>>() {};
    private static final TypeReference<ListPayload<Role>> ROLES_TR = new TypeReference<ListPayload<Role>>() {};
    private static final TypeReference<ListPayload<Policy>> POLICIES_TR = new TypeReference<ListPayload<Policy>>() {};
    private static final TypeReference<ListPayload<Entity>> ENTITIES_TR = new TypeReference<ListPayload<Entity>>() {};

    private final String endpoint;
    private final HttpOperator http;

    public RbacClient(final String endpoint) {
        this(endpoint, new SyncHttpOperator());
    }

    public RbacClient(final String endpoint, final HttpOperator http) {
        validateStringNotBlank(endpoint, "rbac service endpoint");
        validateObjectNotNull(http, "http operator");
        log.debug("initializing client for rbac endpoint {}...", endpoint);
        this.endpoint = endpoint;
        this.http = http;
    }

    public User getUser(String userId) throws Exception {
        validateStringNotBlank(userId, "user id");
        log.debug("retrieving user {}...", userId);
        User user = HttpExecution.get(endpoint + "/users/" + userId).executeForJson(http, User.class);
        log.debug("retrieved user {}", user);
        return user;
    }

    public ListPayload<User> listUsers(long offset, long limit, String search) throws Exception {
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing users with offset {} limit {} and search {}...", offset, limit, search);
        ListPayload<User> lp = HttpExecution.get(endpoint + "/users").param("offset", String.valueOf(offset)).param("limit", String.valueOf(limit))
                .param("search", search).executeForJson(http, USERS_TR);
        log.debug("listed users {}", lp);
        return lp;
    }

    public ListPayload<User> listUsersOfRole(long roleId, long offset, long limit) throws Exception {
        validateLongPositive(roleId, "role id");
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing users assigned with role {} with offset {} and limit {}...", roleId, offset, limit);
        ListPayload<User> lp = HttpExecution.get(endpoint + "/roles/" + roleId + "/users").param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit)).executeForJson(http, USERS_TR);
        log.debug("listed role users {}", lp);
        return lp;
    }

    public Role createRole(Role role) throws Exception {
        validateObjectNotNull(role, "role");
        log.debug("creating role {}...", role);
        Role created = HttpExecution.post(endpoint + "/admin/roles").jsonBody(role).executeForJson(http, Role.class);
        log.debug("created role {}", role);
        return created;
    }

    public Role getRole(long roleId) throws Exception {
        validateLongPositive(roleId, "role id");
        log.debug("retrieving role {}...", roleId);
        Role role = HttpExecution.get(endpoint + "/roles/" + roleId).executeForJson(http, Role.class);
        log.debug("retrieved role {}");
        return role;
    }

    public ListPayload<Role> listsRoles(long offset, long limit, String search) throws Exception {
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing roles with offset {} limit {} and search {}...", offset, limit, search);
        ListPayload<Role> lp = HttpExecution.get(endpoint + "/roles").param("offset", String.valueOf(offset)).param("limit", String.valueOf(limit))
                .param("search", search).executeForJson(http, ROLES_TR);
        log.debug("listed roles {}", lp);
        return lp;
    }

    public ListPayload<Role> listRolesOfUser(long userId, long offset, long limit) throws Exception {
        validateLongPositive(userId, "user pk id");
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing roles assigned to user {} with offset {} and limit {}...", userId, offset, limit);
        ListPayload<Role> lp = HttpExecution.get(endpoint + "/users/" + userId + "/roles").param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit)).executeForJson(http, ROLES_TR);
        log.debug("listed user roles {}", lp);
        return lp;
    }

    public Role updateRoleDescription(long roleId, String description) throws Exception {
        validateLongPositive(roleId, "role id");
        validateStringNotBlank(description, "role description");
        log.debug("updating description to {} for role {}...", description, roleId);
        Role role = HttpExecution.put(endpoint + "/admin/roles/" + roleId).jsonBody(new DescriptionUpdate(description)).executeForJson(http, Role.class);
        log.debug("updated role {}", role);
        return role;
    }

    public void deleteRole(long roleId) throws Exception {
        validateLongPositive(roleId, "role id");
        log.debug("deleting role {}...", roleId);
        HttpExecution.delete(endpoint + "/admin/roles/" + roleId).executeVoid(http);
        log.debug("deleted role {}", roleId);
    }

    public Policy createPolicy(Policy policy) throws Exception {
        validateObjectNotNull(policy, "policy");
        log.debug("creating policy {}...", policy);
        Policy created = HttpExecution.post(endpoint + "/admin/policies").jsonBody(policy).executeForJson(http, Policy.class);
        log.debug("created policy {}", policy);
        return created;
    }

    public Policy getPolicy(long policyId) throws Exception {
        validateLongPositive(policyId, "policy id");
        log.debug("retrieving policy {}...", policyId);
        Policy policy = HttpExecution.get(endpoint + "/policies/" + policyId).executeForJson(http, Policy.class);
        log.debug("retrieved policy {}");
        return policy;
    }

    public ListPayload<Policy> listsPolicies(long offset, long limit, String search) throws Exception {
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing policies with offset {} limit {} and search {}...", offset, limit, search);
        ListPayload<Policy> lp = HttpExecution.get(endpoint + "/policies").param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit)).param("search", search).executeForJson(http, POLICIES_TR);
        log.debug("listed policies {}", lp);
        return lp;
    }

    public ListPayload<Policy> listPoliciesOfUser(long userId, long offset, long limit) throws Exception {
        validateLongPositive(userId, "user pk id");
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing policies assigned to user {} with offset {} and limit {}...", userId, offset, limit);
        ListPayload<Policy> lp = HttpExecution.get(endpoint + "/users/" + userId + "/policies").param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit)).executeForJson(http, POLICIES_TR);
        log.debug("listed user policies {}", lp);
        return lp;
    }

    public ListPayload<Policy> listPoliciesOfRole(long roleId, long offset, long limit) throws Exception {
        validateLongPositive(roleId, "role id");
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing policies assigned to role {} with offset {} and limit {}...", roleId, offset, limit);
        ListPayload<Policy> lp = HttpExecution.get(endpoint + "/roles/" + roleId + "/policies").param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit)).executeForJson(http, POLICIES_TR);
        log.debug("listed role policies {}", lp);
        return lp;
    }

    public Policy updatePolicyDescription(long policyId, String description) throws Exception {
        validateLongPositive(policyId, "policy id");
        validateStringNotBlank(description, "policy description");
        log.debug("updating description to {} for policy {}...", description, policyId);
        Policy policy = HttpExecution.put(endpoint + "/admin/policies/" + policyId).jsonBody(new DescriptionUpdate(description)).executeForJson(http, Policy.class);
        log.debug("updated policy {}", policy);
        return policy;
    }

    public void deletePolicy(long policyId) throws Exception {
        validateLongPositive(policyId, "policy id");
        log.debug("deleting policy {}...", policyId);
        HttpExecution.delete(endpoint + "/admin/policies/" + policyId).executeVoid(http);
        log.debug("deleted policy {}", policyId);
    }

    public ListPayload<Entity> listEntitiesWithPolicy(long policyId, long offset, long limit) throws Exception {
        validateLongPositive(policyId, "policy id");
        validateLongNotNegative(offset, "listing offset");
        validateLongNotNegative(limit, "listing limit");
        log.debug("listing entities assigned with policy {} offset {} limit {}...", policyId, offset, limit);
        ListPayload<Entity> lp = HttpExecution.get(endpoint + "/policies/" + policyId + "/entities").param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit)).executeForJson(http, ENTITIES_TR);
        log.debug("listed entities {}", lp);
        return lp;
    }

    public void assignRoleToUser(long roleId, String userId) throws Exception {
        validateLongPositive(roleId, "role id");
        validateStringNotBlank(userId, "user id");
        log.debug("assigning role {} to user {}...", roleId, userId);
        HttpExecution.post(endpoint + "/admin/roles/" + roleId + "/users/" + userId).executeVoid(http);
        log.debug("assigned role {} to user {}", roleId, userId);
    }

    public void unassignRoleFromUser(long roleId, String userId) throws Exception {
        validateLongPositive(roleId, "role id");
        validateStringNotBlank(userId, "user id");
        log.debug("unassigning role {} from user {}...", roleId, userId);
        HttpExecution.delete(endpoint + "/admin/roles/" + roleId + "/users/" + userId).executeVoid(http);
        log.debug("unassigned role {} from user {}", roleId, userId);
    }

    public void assignPolicyToUser(long policyId, String userId) throws Exception {
        validateLongPositive(policyId, "policy id");
        validateStringNotBlank(userId, "user id");
        log.debug("assigning policy {} to user {}...", policyId, userId);
        HttpExecution.post(endpoint + "/admin/policies/" + policyId + "/users/" + userId).executeVoid(http);
        log.debug("assigned policy {} to user {}", policyId, userId);
    }

    public void unassignPolicyFromUser(long policyId, String userId) throws Exception {
        validateLongPositive(policyId, "policy id");
        validateStringNotBlank(userId, "user id");
        log.debug("unassigning policy {} from user {}...", policyId, userId);
        HttpExecution.delete(endpoint + "/admin/policies/" + policyId + "/users/" + userId).executeVoid(http);
        log.debug("unassigned policy {} from user {}", policyId, userId);
    }

    public void assignPolicyToRole(long policyId, long roleId) throws Exception {
        validateLongPositive(policyId, "policy id");
        validateLongPositive(roleId, "role id");
        log.debug("assigning policy {} to role {}...", policyId, roleId);
        HttpExecution.post(endpoint + "/admin/policies/" + policyId + "/roles/" + roleId).executeVoid(http);
        log.debug("assigned policy {} to role {}", policyId, roleId);
    }

    public void unassignPolicyFromRole(long policyId, long roleId) throws Exception {
        validateLongPositive(policyId, "policy id");
        validateLongPositive(roleId, "role id");
        log.debug("unassigning policy {} from role {}...", policyId, roleId);
        HttpExecution.delete(endpoint + "/admin/policies/" + policyId + "/roles/" + roleId).executeVoid(http);
        log.debug("unassigned policy {} from role {}", policyId, roleId);
    }

    @Override
    public AuthResponse authorize(AuthRequest request) throws Exception {
        validateObjectNotNull(request, "auth request");
        log.debug("authorizing {}...", request);
        AuthResponse response = HttpExecution.post(endpoint + "/auth").jsonBody(request).executeForJson(http, AuthResponse.class);
        log.debug("authorized with {}", response);
        return response;
    }
}
