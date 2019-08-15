package com.bosh.rbac.model;

import lombok.Getter;

import static org.mib.common.validator.Validator.validateIntPositive;

public enum ResourceType implements IntEnum {

    HDFS(1), COLUMN(2), TAG(3), OPERATION(4);

    private final @Getter int value;

    ResourceType(final int value) {
        validateIntPositive(value, "resource type value");
        this.value = value;
    }
}
