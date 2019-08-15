package com.bosh.rbac.service;

import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.Policy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static org.mib.common.validator.Validator.validateObjectNotNull;

@Slf4j
@Service
public class PolicyPredicate {

    boolean pass(Policy policy, ResourceAccess resourceAccess) {
        validateObjectNotNull(policy, "policy");
        validateObjectNotNull(resourceAccess, "resource access");
        Action action = resourceAccess.getAction();
        validateObjectNotNull(action, "action");
        log.debug("testing policy {} for action {}...", policy, action);
        boolean pass;
        if (policy.getAction() == action) {
            pass = true;
        } else {
            switch (action) {
                case Read:
                    pass = policy.getAction() != Action.Operate;
                    break;
                case Write:
                    pass = false;
                    break;
                case Operate:
                    pass = false;
                    break;
                default:
                    pass = false;
            }
        }
        log.debug("policy {} for action {} testing result {}", policy, action, pass);
        return pass;
    }
}
