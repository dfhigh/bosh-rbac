package com.bosh.rbac.context;

import com.bosh.rbac.model.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.mib.common.validator.Validator.validateStringNotBlank;

@Getter
@ToString
@EqualsAndHashCode
public class RbacContext {

    private final String requestId;
    private final String userId;
    private @Setter User user;

    public RbacContext(final String requestId, final String userId) {
        validateStringNotBlank(requestId, "request id");
        this.requestId = requestId;
        this.userId = userId;
    }
}
