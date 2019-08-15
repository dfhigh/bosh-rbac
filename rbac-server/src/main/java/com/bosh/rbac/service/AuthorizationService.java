package com.bosh.rbac.service;

import com.bosh.rbac.auth.model.AuthRequest;
import com.bosh.rbac.auth.model.AuthResponse;
import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.model.Resource;
import com.bosh.rbac.service.resource.ResourceDecorator;
import com.bosh.rbac.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.mib.common.validator.Validator.validateCollectionNotEmptyContainsNoNull;
import static org.mib.common.validator.Validator.validateObjectNotNull;

@Slf4j
@Service
public class AuthorizationService {

    private final Authorizer authorizer;
    private final ResourceDecorator resourceDecorator;

    @Autowired
    public AuthorizationService(final Authorizer authorizer,
                                @Qualifier("resourceDecorator") final ResourceDecorator resourceDecorator) {
        this.authorizer = authorizer;
        this.resourceDecorator = resourceDecorator;
    }

    public AuthResponse authorize(AuthRequest request) {
        validateObjectNotNull(request, "auth request");
        List<ResourceAccess> resourceAccesses = request.getResourceAccesses();
        validateCollectionNotEmptyContainsNoNull(resourceAccesses, "resource accesses");
        resourceAccesses.forEach(Validator::validateResourceAccess);
        // decorate resources
        resourceAccesses.forEach(resourceAccess -> {
            Resource resource = resourceAccess.getResource();
            log.debug("decorating resource {}...", resource);
            List<Resource> decoratedResources = resourceDecorator.decorate(resource);
            log.debug("decorated with {}", decoratedResources);
            if (decoratedResources == null || decoratedResources.isEmpty()) return;
            if (resourceAccess.getDecoratedResources() == null) resourceAccess.setDecoratedResources(decoratedResources);
            else resourceAccess.getDecoratedResources().addAll(decoratedResources);
        });
        log.debug("authorizing request {}...", request);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setResults(resourceAccesses.stream().map(authorizer::authorize).collect(Collectors.toList()));
        log.debug("authorized response {}", authResponse);
        return authResponse;
    }
}
