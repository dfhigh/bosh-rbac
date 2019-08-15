package com.bosh.rbac.service.resource;

import com.bosh.rbac.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.mib.common.validator.Validator.validateObjectNotNull;

@Slf4j
@Service
@Qualifier("resourceDecorator")
public class HybridResourceDecorator implements ResourceDecorator {

    private final HDFSResourceDecorator hdfsResourceDecorator;
    private final ColumnResourceDecorator columnResourceDecorator;

    @Autowired
    public HybridResourceDecorator(final HDFSResourceDecorator hdfsResourceDecorator,
                                   final ColumnResourceDecorator columnResourceDecorator) {
        this.hdfsResourceDecorator = hdfsResourceDecorator;
        this.columnResourceDecorator = columnResourceDecorator;
    }

    @Override
    public List<Resource> decorate(Resource resource) {
        validateObjectNotNull(resource, "resource");
        validateObjectNotNull(resource.getType(), "resource type");
        switch (resource.getType()) {
            case HDFS:
                return hdfsResourceDecorator.decorate(resource);
            case COLUMN:
                return columnResourceDecorator.decorate(resource);
            default:
                return EMPTY;
        }
    }
}
