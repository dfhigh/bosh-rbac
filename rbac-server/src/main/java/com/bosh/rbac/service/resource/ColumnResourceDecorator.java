package com.bosh.rbac.service.resource;

import com.bosh.rbac.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mib.rest.client.HttpOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ColumnResourceDecorator implements ResourceDecorator {

    private final HttpOperator http;

    @Autowired
    public ColumnResourceDecorator(final HttpOperator http) {
        this.http = http;
    }

    @Override
    public List<Resource> decorate(Resource resource) {
        // TODO: call API to retrieve tags
        return EMPTY;
    }
}
