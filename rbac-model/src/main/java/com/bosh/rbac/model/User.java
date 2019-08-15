package com.bosh.rbac.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends Entity {

    private String userId;
    private String username;
    private boolean admin;
    private Date createdAt;

    public User() {
        setType(EntityType.User);
    }
}
