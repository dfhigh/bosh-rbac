package com.bosh.rbac.annotation.component;

import com.bosh.rbac.auth.model.AuthResponse;
import com.bosh.rbac.auth.model.ResourceAccessAuth;
import com.bosh.rbac.client.RbacAuthorizer;
import com.bosh.rbac.client.RbacClient;
import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.Policy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Configuration
public class RbacAuthorizerBean {

    @Value("${rbac.mock:false}")
    private boolean isMock;
    @Value("${rbac.mock.rate:0.5}")
    private double allowRate;
    @Value("${rbac.endpoint}")
    private String rbacEndpoint;

    @Bean
    public RbacAuthorizer rbacAuthorizer() {
        return isMock ? createMockAuthorizer() : new RbacClient(rbacEndpoint);
    }

    private RbacAuthorizer createMockAuthorizer() {
        return request -> {
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            AuthResponse response = new AuthResponse();
            List<ResourceAccessAuth> resourceAccessAuths = request.getResourceAccesses().stream().map(ra -> {
                ResourceAccessAuth raa = new ResourceAccessAuth();
                raa.setResourceAccess(ra);
                if (tlr.nextDouble() < allowRate) {
                    raa.setAllow(true);
                    raa.setAllowedAsEntity(new Entity(tlr.nextDouble() < 0.5 ? EntityType.Role : EntityType.User, tlr.nextLong(100)));
                    Policy policy = new Policy();
                    policy.setResource(ra.getResource());
                    policy.setAction(ra.getAction());
                    policy.setDescription(randomAlphanumeric(100));
                    policy.setName(randomAlphanumeric(20));
                    policy.setId(tlr.nextLong(100));
                    policy.setCreatedAt(new Date());
                    policy.setUpdatedAt(new Date());
                    raa.setAllowedByPolicy(policy);
                }
                return raa;
            }).collect(Collectors.toList());
            response.setResults(resourceAccessAuths);
            return response;
        };
    }
}
