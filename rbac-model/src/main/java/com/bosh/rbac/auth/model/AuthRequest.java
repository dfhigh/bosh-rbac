package com.bosh.rbac.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private List<ResourceAccess> resourceAccesses;
}
