package com.bosh.rbac.auth.model;

import com.bosh.rbac.model.Entity;
import com.bosh.rbac.model.Policy;
import lombok.Data;

@Data
public class ResourceAccessAuth {
    private boolean allow;
    private Entity allowedAsEntity;
    private Policy allowedByPolicy;
    private ResourceAccess resourceAccess;
}
