package com.bosh.rbac.service.resource;

import com.bosh.rbac.model.Resource;
import com.google.common.collect.ImmutableList;

import java.util.List;

public interface ResourceDecorator {

    List<Resource> EMPTY = ImmutableList.of();

    default List<Resource> decorate(Resource resource) {
        return EMPTY;
    }
}
