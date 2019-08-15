package com.bosh.rbac.auth.model;

import lombok.Data;

import java.util.List;

@Data
public class AuthRequest {
    private List<ResourceAccess> resourceAccesses;
}
