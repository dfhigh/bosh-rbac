package com.bosh.rbac.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityPolicy {
    private EntityType entityType;
    private long entityId;
    private long policyId;
    private Date createdAt;
}
