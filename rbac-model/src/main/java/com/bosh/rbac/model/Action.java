package com.bosh.rbac.model;

import lombok.Getter;

import static org.mib.common.validator.Validator.validateIntPositive;

public enum Action implements IntEnum {

    Read(1), Write(2), Operate(3);

    private @Getter final int value;

    Action(final int value) {
        validateIntPositive(value, "action value");
        this.value = value;
    }
}
