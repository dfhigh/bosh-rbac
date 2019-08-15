package com.bosh.rbac.model;

import lombok.Data;

import java.util.Date;

@Data
public class Policy {
    private long id;
    private String name;
    private String description;
    private Resource resource;
    private Action action;
    private Date createdAt;
    private Date updatedAt;
}
