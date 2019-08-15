package com.bosh.rbac.service;

import com.bosh.rbac.context.RbacScope;
import com.bosh.rbac.mapper.RbacMapper;
import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.EntityPolicy;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.model.User;
import com.bosh.rbac.model.UserRole;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.mib.rest.exception.BadRequestException;
import org.mib.rest.exception.ForbiddenException;
import org.mib.rest.exception.ResourceNotFoundException;
import org.mib.rest.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bosh.rbac.utils.Validator.validatePolicy;
import static com.bosh.rbac.utils.Validator.validateRole;
import static org.mib.common.validator.Validator.validateLongPositive;
import static org.mib.common.validator.Validator.validateStringNotBlank;

@Slf4j
@Service
public class AdminService {

    private final RbacMapper mapper;

    @Autowired
    public AdminService(final RbacMapper mapper) {
        this.mapper = mapper;
    }

    public Role createRole(Role role) {
        ensureAdmin();
        validateRole(role);
        Date now = new Date();
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        log.debug("creating role {}...", role);
        mapper.insertRole(role);
        log.debug("created role with id {}", role.getId());
        return role;
    }

    public Role updateRoleDescription(long id, String description) {
        ensureAdmin();
        validateLongPositive(id, "role id");
        validateStringNotBlank(description, "role description");
        log.debug("updating role {} with description {}...", id, description);
        Role role = mapper.getRole(id);
        if (role == null) throw new ResourceNotFoundException("no role found for id " + id);
        role.setUpdatedAt(new Date());
        role.setDescription(description);
        if (mapper.updateRole(role) != 1) throw new RuntimeException("failed to update role " + id + " description");
        log.debug("updated description for role {}", id);
        return role;
    }

    public void deleteRole(long id) {
        ensureAdmin();
        validateLongPositive(id, "role id");
        log.debug("deleting role {}...", id);
        // check users assigned with this role
        long userCount = mapper.countRoleUsers(id);
        log.debug("role {} assigned to {} users", id, userCount);
        if (userCount > 0) throw new BadRequestException("role still assigned to users");
        // check policies assigned to this role
        long policyCount = mapper.countEntityPolicies(new Entity(EntityType.Role, id));
        log.debug("role {} assigned with {} policies", id, policyCount);
        if (policyCount > 0) throw new BadRequestException("role still assigned with policies");
        int deleted = mapper.deleteRoles(Lists.newArrayList(id));
        log.debug("deleted {} role", deleted);
    }

    public Policy createPolicy(Policy policy) {
        ensureAdmin();
        validatePolicy(policy);
        Date now = new Date();
        policy.setCreatedAt(now);
        policy.setUpdatedAt(now);
        log.debug("creating policy {}...", policy);
        mapper.insertPolicy(policy);
        log.debug("created policy with id {}", policy.getId());
        return policy;
    }

    public Policy updatePolicyDescription(long id, String description) {
        ensureAdmin();
        validateLongPositive(id, "policy id");
        validateStringNotBlank(description, "policy description");
        log.debug("updating policy {} with description {}...", id, description);
        Policy policy = mapper.getPolicy(id);
        if (policy == null) throw new ResourceNotFoundException("no policy found for id " + id);
        policy.setUpdatedAt(new Date());
        policy.setDescription(description);
        if (mapper.updatePolicy(policy) != 1) throw new RuntimeException("failed to update policy " + id + " description");
        log.debug("updated description for policy {}", id);
        return policy;
    }

    public void deletePolicy(long id) {
        ensureAdmin();
        validateLongPositive(id, "policy id");
        log.debug("deleting policy {}...", id);
        // check entities assigned with this policy
        long entityCount = mapper.countPolicyEntities(id);
        log.debug("policy {} assigned to {} entities", id, entityCount);
        if (entityCount > 0) throw new BadRequestException("policy still assigned to entities");
        int deleted = mapper.deletePolicies(Lists.newArrayList(id));
        log.debug("deleted {} policy", deleted);
    }

    public void assignRoleToUser(String userId, long roleId) {
        ensureAdmin();
        validateStringNotBlank(userId, "user id");
        validateLongPositive(roleId, "role id");
        log.debug("assigning role {} to user {}...", roleId, userId);
        User user = mapper.getUser(userId);
        if (user == null) throw new ResourceNotFoundException("no user found for " + userId);
        Role role = mapper.getRole(roleId);
        if (role == null) throw new ResourceNotFoundException("no role found for " + roleId);
        UserRole userRole = mapper.getUserRole(user.getId(), roleId);
        if (userRole != null) {
            log.debug("role {} already assigned to user {}", roleId, userId);
            return;
        }
        if (mapper.insertUserRole(user.getId(), roleId) != 1){
            throw new RuntimeException("failed to assign role " + role.getName() + " to user " + userId);
        }
        log.debug("assigned role {} to user {}", role.getName(), userId);
    }

    public void unassignRoleFromUser(String userId, long roleId) {
        ensureAdmin();
        validateStringNotBlank(userId, "user id");
        validateLongPositive(roleId, "role id");
        log.debug("unassigning role {} from user {}...", roleId, userId);
        User user = mapper.getUser(userId);
        if (user == null) throw new ResourceNotFoundException("no user found for " + userId);
        Role role = mapper.getRole(roleId);
        if (role == null) throw new ResourceNotFoundException("no role found for " + roleId);
        UserRole userRole = mapper.getUserRole(user.getId(), roleId);
        if (userRole == null) {
            log.debug("role {} not assigned to user {}", roleId, userId);
            return;
        }
        if (mapper.deleteUserRole(user.getId(), roleId) != 1){
            throw new RuntimeException("failed to unassign role " + role.getName() + " from user " + userId);
        }
        log.debug("unassigned role {} from user {}", role.getName(), userId);
    }

    public void assignPolicyToUser(String userId, long policyId) {
        ensureAdmin();
        validateStringNotBlank(userId, "user id");
        validateLongPositive(policyId, "policy id");
        log.debug("assigning policy {} to user {}...", policyId, userId);
        User user = mapper.getUser(userId);
        if (user == null) throw new ResourceNotFoundException("no user found for " + userId);
        Policy policy = mapper.getPolicy(policyId);
        if (policy == null) throw new ResourceNotFoundException("no policy found for " + policyId);
        EntityPolicy entityPolicy = mapper.getEntityPolicy(EntityType.User, user.getId(), policyId);
        if (entityPolicy != null) {
            log.debug("policy {} already assigned to user {}", policy.getName(), userId);
            return;
        }
        if (mapper.insertEntityPolicy(EntityType.User, user.getId(), policyId) != 1) {
            throw new RuntimeException("failed to assign policy " + policy.getName() + " to user " + userId);
        }
        log.debug("assigned policy {} to user {}", policy.getName(), userId);
    }

    public void unassignPolicyFromUser(String userId, long policyId) {
        ensureAdmin();
        validateStringNotBlank(userId, "user id");
        validateLongPositive(policyId, "policy id");
        log.debug("unassigning policy {} from user {}...", policyId, userId);
        User user = mapper.getUser(userId);
        if (user == null) throw new ResourceNotFoundException("no user found for " + userId);
        Policy policy = mapper.getPolicy(policyId);
        if (policy == null) throw new ResourceNotFoundException("no policy found for " + policyId);
        EntityPolicy entityPolicy = mapper.getEntityPolicy(EntityType.User, user.getId(), policyId);
        if (entityPolicy == null) {
            log.debug("policy {} not assigned to user {}", policy.getName(), userId);
            return;
        }
        if (mapper.deleteEntityPolicy(EntityType.User, user.getId(), policyId) != 1) {
            throw new RuntimeException("failed to unassign policy " + policy.getName() + " from user " + userId);
        }
        log.debug("unassigned policy {} from user {}", policy.getName(), userId);
    }

    public void assignPolicyToRole(long roleId, long policyId) {
        ensureAdmin();
        validateLongPositive(roleId, "role id");
        validateLongPositive(policyId, "policy id");
        log.debug("assigning policy {} to role {}...", policyId, roleId);
        Role role = mapper.getRole(roleId);
        if (role == null) throw new ResourceNotFoundException("no role found for " + roleId);
        Policy policy = mapper.getPolicy(policyId);
        if (policy == null) throw new ResourceNotFoundException("no policy found for " + policyId);
        EntityPolicy entityPolicy = mapper.getEntityPolicy(EntityType.Role, roleId, policyId);
        if (entityPolicy != null) {
            log.debug("policy {} already assigned to role {}", policy.getName(), role.getName());
            return;
        }
        if (mapper.insertEntityPolicy(EntityType.Role, roleId, policyId) != 1) {
            throw new RuntimeException("failed to assign policy " + policy.getName() + " to role " + role.getName());
        }
        log.debug("assigned policy {} to role {}", policy.getName(), role.getName());
    }

    public void unassignPolicyFromRole(long roleId, long policyId) {
        ensureAdmin();
        validateLongPositive(roleId, "role id");
        validateLongPositive(policyId, "policy id");
        log.debug("unassigning policy {} from role {}...", policyId, roleId);
        Role role = mapper.getRole(roleId);
        if (role == null) throw new ResourceNotFoundException("no role found for " + roleId);
        Policy policy = mapper.getPolicy(policyId);
        if (policy == null) throw new ResourceNotFoundException("no policy found for " + policyId);
        EntityPolicy entityPolicy = mapper.getEntityPolicy(EntityType.Role, roleId, policyId);
        if (entityPolicy == null) {
            log.debug("policy {} not assigned to role {}", policy.getName(), roleId);
            return;
        }
        if (mapper.deleteEntityPolicy(EntityType.Role, roleId, policyId) != 1) {
            throw new RuntimeException("failed to unassign policy " + policy.getName() + " from role " + role.getName());
        }
        log.debug("unassigned policy {} from user {}", policy.getName(), role.getName());
    }

    private void ensureAdmin() {
        User user = RbacScope.getUser();
        if (user == null) throw new UnauthorizedException("no user in context");
        if (!user.isAdmin()) throw new ForbiddenException("not authorized to perform this operation");
    }
}
