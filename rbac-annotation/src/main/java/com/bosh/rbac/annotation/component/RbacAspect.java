package com.bosh.rbac.annotation.component;

import com.bosh.rbac.annotation.RBAC;
import com.bosh.rbac.annotation.RuntimeResourceAccess;
import com.bosh.rbac.auth.model.AuthRequest;
import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.auth.model.ResourceAccessAuth;
import com.bosh.rbac.client.RbacAuthorizer;
import com.bosh.rbac.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.mib.rest.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mib.common.validator.Validator.validateCollectionNotEmptyContainsNoNull;
import static org.mib.common.validator.Validator.validateObjectNotNull;

@Slf4j
@Aspect
@Component
public class RbacAspect {

    private final RbacAuthorizer authorizer;

    @Autowired
    public RbacAspect(final RbacAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    @Around("@annotation(com.bosh.rbac.annotation.RBAC)")
    public Object rbac(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RBAC rbac = method.getAnnotation(RBAC.class);
        if (rbac == null) return joinPoint.proceed();
        log.debug("authorizing method {}.{} with decision policy {}...", method.getDeclaringClass().getCanonicalName(),
                method.getName(), rbac.decisionPolicy());
        List<ResourceAccess> resourceAccesses = Arrays.stream(rbac.value()).map(this::convert).collect(Collectors.toList());

        Parameter[] parameters = method.getParameters();
        Object[] arguments = joinPoint.getArgs();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            RuntimeResourceAccess resourceAccessAnnotation = parameter.getAnnotation(RuntimeResourceAccess.class);
            if (resourceAccessAnnotation == null) continue;
            Object argument = arguments[i];
            validateObjectNotNull(argument, "resource value");
            ResourceAccess resourceAccess = new ResourceAccess();
            resourceAccess.setAction(resourceAccessAnnotation.action());
            resourceAccess.setResource(new Resource(resourceAccessAnnotation.resourceType(), argument.toString()));
            resourceAccesses.add(resourceAccess);
        }
        validateCollectionNotEmptyContainsNoNull(resourceAccesses, "resource access list");
        log.debug("authorizing resource accesses {}...", resourceAccesses);
        List<ResourceAccessAuth> authorizedResults = authorizer.authorize(new AuthRequest(resourceAccesses)).getResults();
        log.debug("authorized resource accesses {}", authorizedResults);
        boolean allow;
        Stream<ResourceAccessAuth> stream = authorizedResults.stream().filter(ResourceAccessAuth::isAllow);
        if (rbac.decisionPolicy() == RBAC.DecisionPolicy.ALL) {
            long allowCount = stream.count();
            allow = allowCount == resourceAccesses.size();
        } else {
            ResourceAccessAuth raa = stream.findFirst().orElse(null);
            allow = raa != null;
        }
        if (!allow) throw new ForbiddenException("not authorized to access resources");
        return joinPoint.proceed();
    }

    private ResourceAccess convert(RBAC.StaticResourceAccess ra) {
        ResourceAccess resourceAccess = new ResourceAccess();
        resourceAccess.setAction(ra.operation().action());
        resourceAccess.setResource(new Resource(ra.operation().resourceType(), ra.value()));
        return resourceAccess;
    }
}
