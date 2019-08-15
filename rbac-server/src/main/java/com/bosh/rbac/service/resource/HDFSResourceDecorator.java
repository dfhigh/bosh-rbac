package com.bosh.rbac.service.resource;

import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.ResourceType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.mib.common.validator.Validator.validateObjectNotNull;
import static org.mib.common.validator.Validator.validateStringNotBlank;

@Slf4j
@Service
public class HDFSResourceDecorator implements ResourceDecorator {

    @Override
    public List<Resource> decorate(Resource resource) {
        validateObjectNotNull(resource, "resource");
        if (resource.getType() != ResourceType.HDFS) return EMPTY;
        String path = resource.getValue();
        validateStringNotBlank(path, "hdfs resource path");
        log.debug("decorating resources for hdfs resource {}...", resource);
        String prefix = Strings.EMPTY;
        if (path.contains("://")) {
            int index = path.indexOf("://");
            prefix = path.substring(0, index + 3);
            path = path.substring(index + 3);
        }
        String[] fields = path.split("/");
        if (fields.length <= 1) return EMPTY;
        String previous = prefix;
        List<Resource> decorated = Lists.newArrayListWithCapacity(fields.length-1);
        for (int i = 0; i < fields.length - 1; i++) {
            if (fields[i].isEmpty()) continue;
            String p = previous;
            if (previous.length() > prefix.length()) p += "/";
            p += fields[i];
            decorated.add(new Resource(ResourceType.HDFS, p));
            previous = p;
        }
        log.debug("decorated with {}", decorated);
        return decorated;
    }
}
