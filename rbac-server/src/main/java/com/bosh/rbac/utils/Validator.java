package com.bosh.rbac.utils;

import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.Role;

import java.util.List;

import static org.mib.common.validator.Validator.validateObjectNotNull;
import static org.mib.common.validator.Validator.validateStringNotBlank;

public class Validator {

    private Validator() {}

    public static void validateResource(Resource resource) {
        validateObjectNotNull(resource, "resource");
        validateObjectNotNull(resource.getType(), "resource type");
        validateStringNotBlank(resource.getValue(), "resource value");
    }

    public static void validateResourceAccess(ResourceAccess resourceAccess) {
        validateObjectNotNull(resourceAccess, "resource access");
        validateObjectNotNull(resourceAccess.getAction(), "resource access action");
        validateResource(resourceAccess.getResource());
        List<Resource> decoratedResources = resourceAccess.getDecoratedResources();
        if (decoratedResources != null) {
            decoratedResources.forEach(Validator::validateResource);
        }
    }

    public static void validateRole(Role role) {
        validateObjectNotNull(role, "role");
        validateStringNotBlank(role.getName(), "role name");
    }

    public static void validatePolicy(Policy policy) {
        validateObjectNotNull(policy, "policy");
        validateStringNotBlank(policy.getName(), "policy name");
        validateResource(policy.getResource());
        validateObjectNotNull(policy.getAction(), "policy action");
    }
}
