package com.bosh.rbac.test.service;

import com.bosh.rbac.annotation.RBAC;
import com.bosh.rbac.annotation.RuntimeResourceAccess;
import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.ResourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {

    @RBAC({
            @RBAC.StaticResourceAccess(
                    operation = @RuntimeResourceAccess(resourceType = ResourceType.HDFS, action = Action.Read),
                    value = "hdfs://localhost:8020/home/work/data1"
            ),
            @RBAC.StaticResourceAccess(
                    operation = @RuntimeResourceAccess(resourceType = ResourceType.HDFS, action = Action.Write),
                    value = "hdfs://localhost:8020/home/work/data2"
            )
    })
    public String test1() {
        return "hello world";
    }

    @RBAC({
            @RBAC.StaticResourceAccess(
                    operation = @RuntimeResourceAccess(resourceType = ResourceType.HDFS, action = Action.Read),
                    value = "hdfs://localhost:8020/home/work/data1"
            ),
            @RBAC.StaticResourceAccess(
                    operation = @RuntimeResourceAccess(resourceType = ResourceType.HDFS, action = Action.Write),
                    value = "hdfs://localhost:8020/home/work/data2"
            )
    })
    public String test2(@RuntimeResourceAccess(resourceType = ResourceType.HDFS, action = Action.Read) String name) {
        return "hello " + name;
    }

    @RBAC
    public String test3(@RuntimeResourceAccess(resourceType = ResourceType.TAG, action = Action.Operate) String tag) {
        return "fuck " + tag;
    }
}
