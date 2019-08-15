package com.bosh.rbac.model;

import lombok.Getter;

import static org.mib.common.validator.Validator.validateIntPositive;

public enum EntityType implements IntEnum {

    User(1), Role(2);

    private final @Getter int value;

    EntityType(final int value) {
        validateIntPositive(value, "entity type value");
        this.value = value;
    }
}

