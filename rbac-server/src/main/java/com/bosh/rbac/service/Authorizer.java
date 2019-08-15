package com.bosh.rbac.service;

import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.auth.model.ResourceAccessAuth;
import com.bosh.rbac.context.RbacScope;
import com.bosh.rbac.mapper.RbacMapper;
import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.EntityPolicy;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bosh.rbac.utils.Validator.validateResourceAccess;

@Slf4j
@Service
public class Authorizer {

    private final RbacMapper mapper;
    private final PolicyPredicate predicate;

    @Autowired
    public Authorizer(final RbacMapper mapper, final PolicyPredicate predicate) {
        this.mapper = mapper;
        this.predicate = predicate;
    }

    ResourceAccessAuth authorize(ResourceAccess resourceAccess) {
        validateResourceAccess(resourceAccess);
        Action action = resourceAccess.getAction();
        int size = 1;
        if (resourceAccess.getDecoratedResources() != null) size += resourceAccess.getDecoratedResources().size();
        List<Resource> resources = Lists.newArrayListWithCapacity(size);
        resources.add(resourceAccess.getResource());
        if (size > 1) resources.addAll(resourceAccess.getDecoratedResources());
        log.debug("authorizing {} access on resources {}...", action, resources);
        // get all policies for the resources
        List<Policy> policies = mapper.getResourcesPolicies(resources);
        // filter out policies that could not auth target action
        policies = policies.stream().filter(p -> predicate.pass(p, resourceAccess)).collect(Collectors.toList());
        List<Long> policyIds = policies.stream().map(Policy::getId).collect(Collectors.toList());
        // get user or role that has been assigned with one of the policies
        User user = RbacScope.getUser();
        EntityPolicy entityPolicy = mapper.getUserPolicy(user.getId(), policyIds);
        ResourceAccessAuth resourceAccessAuth = new ResourceAccessAuth();
        if (entityPolicy != null) {
            resourceAccessAuth.setAllow(true);
            if (entityPolicy.getEntityType() == EntityType.User) {
                resourceAccessAuth.setAllowedAsEntity(user);
            } else {
                resourceAccessAuth.setAllowedAsEntity(mapper.getRole(entityPolicy.getEntityId()));
            }
            resourceAccessAuth.setAllowedByPolicy(policies.stream().filter(
                    p -> p.getId() == entityPolicy.getPolicyId()
            ).findFirst().orElse(null));
        }
        resourceAccessAuth.setResourceAccess(resourceAccess);
        return resourceAccessAuth;
    }
}
