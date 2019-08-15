package com.bosh.rbac.auth.model;

import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.Resource;
import lombok.Data;

import java.util.List;

@Data
public class ResourceAccess {
    private Action action;
    private Resource resource;
    private List<Resource> decoratedResources;
}
