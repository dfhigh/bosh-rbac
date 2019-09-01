package com.bosh.rbac.annotation;

import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.ResourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeResourceAccess {

    ResourceType resourceType();

    Action action();
}
