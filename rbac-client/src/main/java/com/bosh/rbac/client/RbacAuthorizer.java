package com.bosh.rbac.client;

import com.bosh.rbac.auth.model.AuthRequest;
import com.bosh.rbac.auth.model.AuthResponse;

public interface RbacAuthorizer {

    AuthResponse authorize(AuthRequest request) throws Exception;
}
