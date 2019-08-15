package com.bosh.rbac.controller;

import com.bosh.rbac.component.RestAPIWrapper;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Role;
import com.bosh.rbac.rest.model.DescriptionUpdate;
import com.bosh.rbac.rest.model.Response;
import com.bosh.rbac.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Api(value = "admin APIs", description = "APIs for system administrator")
public class AdminController {

    private final RestAPIWrapper wrapper;
    private final AdminService adminService;

    @Autowired
    public AdminController(final RestAPIWrapper wrapper, final AdminService adminService) {
        this.wrapper = wrapper;
        this.adminService = adminService;
    }

    @PostMapping(value = "/roles", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "create role", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Role>> createRole(
            @ApiParam(required = true, value = "role body")
            @RequestBody Role role
    ) {
        return wrapper.wrap(() -> adminService.createRole(role));
    }

    @PutMapping(value = "/roles/{roleId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "update role description", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Role>> updateRoleDescription(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId,
            @ApiParam(required = true, value = "role description body")
            @RequestBody DescriptionUpdate du
    ) {
        return wrapper.wrap(() -> adminService.updateRoleDescription(roleId, du.getDescription()));
    }

    @DeleteMapping(value = "/roles/{roleId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "delete role", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> deleteRole(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId
    ) {
        return wrapper.wrap(() -> adminService.deleteRole(roleId));
    }

    @PostMapping(value = "/policies", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "create policy", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Policy>> createPolicy(
            @ApiParam(required = true, value = "policy body")
            @RequestBody Policy policy
    ) {
        return wrapper.wrap(() -> adminService.createPolicy(policy));
    }

    @PutMapping(value = "/policies/{policyId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "update policy description", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Policy>> updatePolicyDescription(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId,
            @ApiParam(required = true, value = "policy description body")
            @RequestBody DescriptionUpdate du
    ) {
        return wrapper.wrap(() -> adminService.updatePolicyDescription(policyId, du.getDescription()));
    }

    @DeleteMapping(value = "/policies/{policyId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "delete policy", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> deletePolicy(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId
    ) {
        return wrapper.wrap(() -> adminService.deletePolicy(policyId));
    }

    @PostMapping(value = "/roles/{roleId}/users/{userId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "assign role to user", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> assignRoleToUser(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId,
            @ApiParam(required = true, value = "user id")
            @PathVariable String userId
    ) {
        return wrapper.wrap(() -> adminService.assignRoleToUser(userId, roleId));
    }

    @DeleteMapping(value = "/roles/{roleId}/users/{userId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "unassign role from user", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> unassignRoleFromUser(
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId,
            @ApiParam(required = true, value = "user id")
            @PathVariable String userId
    ) {
        return wrapper.wrap(() -> adminService.unassignRoleFromUser(userId, roleId));
    }

    @PostMapping(value = "/policies/{policyId}/users/{userId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "assign policy to user", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> assignPolicyToUser(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId,
            @ApiParam(required = true, value = "user id")
            @PathVariable String userId
    ) {
        return wrapper.wrap(() -> adminService.assignPolicyToUser(userId, policyId));
    }

    @DeleteMapping(value = "/policies/{policyId}/users/{userId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "unassign policy from user", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> unassignPolicyFromUser(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId,
            @ApiParam(required = true, value = "user id")
            @PathVariable String userId
    ) {
        return wrapper.wrap(() -> adminService.unassignPolicyFromUser(userId, policyId));
    }

    @PostMapping(value = "/policies/{policyId}/roles/{roleId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "assign policy to role", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> assignPolicyToRole(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId,
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId
    ) {
        return wrapper.wrap(() -> adminService.assignPolicyToRole(roleId, policyId));
    }

    @DeleteMapping(value = "/policies/{policyId}/roles/{roleId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "unassign policy from role", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<Object>> unassignPolicyFromRole(
            @ApiParam(required = true, value = "policy id")
            @PathVariable long policyId,
            @ApiParam(required = true, value = "role id")
            @PathVariable long roleId
    ) {
        return wrapper.wrap(() -> adminService.unassignPolicyFromRole(roleId, policyId));
    }
}
