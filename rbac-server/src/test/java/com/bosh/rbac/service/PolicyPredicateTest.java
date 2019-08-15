package com.bosh.rbac.service;

import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.Policy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class PolicyPredicateTest {

    @Mock
    private Policy policy;
    @Mock
    private ResourceAccess resourceAccess;
    @Autowired
    private PolicyPredicate predicate;

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> predicate.pass(null, resourceAccess));
        assertThrows(IllegalArgumentException.class, () -> predicate.pass(policy, null));

        when(resourceAccess.getAction()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> predicate.pass(policy, resourceAccess));
    }

    @Test
    void testPassActionEquals() {
        for (Action action : Action.values()) {
            when(policy.getAction()).thenReturn(action);
            when(resourceAccess.getAction()).thenReturn(action);
            assertTrue(predicate.pass(policy, resourceAccess));
        }
    }

    @Test
    void testWriteAndRead() {
        when(policy.getAction()).thenReturn(Action.Read);
        when(resourceAccess.getAction()).thenReturn(Action.Write);
        assertFalse(predicate.pass(policy, resourceAccess));

        when(policy.getAction()).thenReturn(Action.Write);
        when(resourceAccess.getAction()).thenReturn(Action.Read);
        assertTrue(predicate.pass(policy, resourceAccess));
    }

    @Test
    void testInvalidMatch() {
        when(policy.getAction()).thenReturn(Action.Operate);
        when(resourceAccess.getAction()).thenReturn(Action.Read);
        assertFalse(predicate.pass(policy, resourceAccess));
    }
}
