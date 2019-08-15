package com.bosh.rbac.auth.model;

import lombok.Data;

import java.util.List;

@Data
public class AuthResponse {
    private List<ResourceAccessAuth> results;
}
