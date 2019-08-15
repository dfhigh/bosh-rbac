package com.bosh.rbac.context;

import com.bosh.rbac.model.User;

import static org.mib.common.validator.Validator.validateLongPositive;
import static org.mib.common.validator.Validator.validateObjectNotNull;

public class RbacScope {

    private static final ThreadLocal<RbacContext> CONTEXT_TL = new ThreadLocal<>();

    private RbacScope() {}

    public static RbacContext getContext() {
        RbacContext context = CONTEXT_TL.get();
        if (context == null) throw new IllegalStateException("no rbac context set");
        return context;
    }

    public static RbacContext clearContext() {
        RbacContext context = CONTEXT_TL.get();
        if (context != null) CONTEXT_TL.remove();
        return context;
    }

    public static void setContext(RbacContext context) {
        validateObjectNotNull(context, "rbac context");
        CONTEXT_TL.set(context);
    }

    public static String getRequestId() {
        return getContext().getRequestId();
    }

    public static String getUserId() {
        return getContext().getUserId();
    }

    public static User getUser() {
        return getContext().getUser();
    }

    public static void setUser(User user) {
        validateObjectNotNull(user, "user");
        validateLongPositive(user.getId(), "user id");
        getContext().setUser(user);
    }
}
