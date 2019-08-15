package com.bosh.rbac.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Role extends Entity {

    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;

    public Role() {
        setType(EntityType.Role);
    }
}
