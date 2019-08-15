package com.bosh.rbac.rest.model;

import lombok.Data;

@Data
public class Response<T> {
    private Status status;
    private String errorCode;
    private String message;
    private T body;
}
