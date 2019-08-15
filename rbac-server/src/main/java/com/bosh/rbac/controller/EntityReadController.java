package com.bosh.rbac.controller;

import com.bosh.rbac.component.RestAPIWrapper;
import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.model.User;
import com.bosh.rbac.rest.model.Response;
import com.bosh.rbac.service.EntityReadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.mib.rest.model.list.ListPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "entity RO APIs", description = "user, role, policy related read APIs")
public class EntityReadController {

    private final RestAPIWrapper wrapper;
    private final EntityReadService entityReadService;

    @Autowired
    public EntityReadController(final RestAPIWrapper wrapper, final EntityReadService entityReadService) {
        this.wrapper = wrapper;
        this.entityReadService = entityReadService;
    }

    @GetMapping(value = "/users/{userId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "get user by userId", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<User>> getUser(
            @ApiParam(required = true, value = "user id")
            @PathVariable String userId
    ) {
        return wrapper.wrap(() -> entityReadService.getUser(userId));
    }

    @GetMapping(value = "/users", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list users page", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<User>>> listUsers(
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit,
            @ApiParam("search query by username")
            @RequestParam String search
    ) {
        return wrapper.wrap(() -> entityReadService.listUsers(offset, limit, search));
    }

    @GetMapping(value = "/roles/{roleId}/users", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list users assigned with the role", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<User>>> listUsersForRole(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId,
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit
    ) {
        return wrapper.wrap(() -> entityReadService.listRoleUsers(roleId, offset, limit));
    }

    @GetMapping(value = "/roles/{roleId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "get role by id", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Role>> getRole(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId
    ) {
        return wrapper.wrap(() -> entityReadService.getRole(roleId));
    }

    @GetMapping(value = "/roles", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list roles page", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<Role>>> listRoles(
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit,
            @ApiParam("search query by role name")
            @RequestParam String search
    ) {
        return wrapper.wrap(() -> entityReadService.listRoles(offset, limit, search));
    }

    @GetMapping(value = "/users/{userId}/roles", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list roles assigned to the user", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<Role>>> listRolesForUser(
            @ApiParam(required = true, value = "user id")
            @PathVariable long userId,
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit
    ) {
        return wrapper.wrap(() -> entityReadService.listUserRoles(userId, offset, limit));
    }

    @GetMapping(value = "/policies/{policyId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "get policy by id", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Policy>> getPolicy(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId
    ) {
        return wrapper.wrap(() -> entityReadService.getPolicy(policyId));
    }

    @GetMapping(value = "/policies", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list policies page", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<Policy>>> listPolicies(
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit,
            @ApiParam("search query by policy name")
            @RequestParam String search
    ) {
        return wrapper.wrap(() -> entityReadService.listPolicies(offset, limit, search));
    }

    @GetMapping(value = "/users/{userId}/policies", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list policies assigned to the user", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<Policy>>> listPoliciesForUser(
            @ApiParam(required = true, value = "user id")
            @PathVariable long userId,
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit
    ) {
        return wrapper.wrap(() -> entityReadService.listEntityPolicies(new Entity(EntityType.User, userId), offset, limit));
    }

    @GetMapping(value = "/roles/{roleId}/policies", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list policies assigned to the role", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<Policy>>> listPoliciesForRole(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId,
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit
    ) {
        return wrapper.wrap(() -> entityReadService.listEntityPolicies(new Entity(EntityType.Role, roleId), offset, limit));
    }

    @GetMapping(value = "/policies/{policyId}/entities", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "list entities assigned with the policy", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<ListPayload<Entity>>> listEntitiesForPolicy(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId,
            @ApiParam(required = true, value = "listing offset")
            @RequestParam long offset,
            @ApiParam(required = true, value = "listing limit")
            @RequestParam long limit
    ) {
        return wrapper.wrap(() -> entityReadService.listPolicyEntities(policyId, offset, limit));
    }
}
