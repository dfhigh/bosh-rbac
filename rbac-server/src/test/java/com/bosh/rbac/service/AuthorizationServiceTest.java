package com.bosh.rbac.service;

import com.bosh.rbac.auth.model.AuthRequest;
import com.bosh.rbac.auth.model.AuthResponse;
import com.bosh.rbac.auth.model.ResourceAccess;
import com.bosh.rbac.auth.model.ResourceAccessAuth;
import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.Resource;
import com.bosh.rbac.model.ResourceType;
import com.bosh.rbac.service.resource.ResourceDecorator;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class AuthorizationServiceTest {

    @Mock
    private AuthRequest request;
    @Mock
    private ResourceAccess ra1, ra2;
    @Mock
    private ResourceAccessAuth raa1, raa2;
    @Mock
    private Resource r1, r2, r3, r4, r5;
    @MockBean
    private Authorizer authorizer;
    @MockBean(name = "resourceDecorator")
    private ResourceDecorator resourceDecorator;

    @Autowired
    private AuthorizationService service;

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> service.authorize(null));

        when(request.getResourceAccesses()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(request.getResourceAccesses()).thenReturn(Lists.newArrayList());
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(request.getResourceAccesses()).thenReturn(Lists.newArrayList((ResourceAccess) null));
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(request.getResourceAccesses()).thenReturn(Lists.newArrayList(ra1));
        when(ra1.getAction()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(ra1.getAction()).thenReturn(Action.Read);
        when(ra1.getResource()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(ra1.getResource()).thenReturn(r1);
        when(r1.getType()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(r1.getType()).thenReturn(ResourceType.COLUMN);
        when(r1.getValue()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(r1.getValue()).thenReturn("");
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));

        when(r1.getValue()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> service.authorize(request));
    }

    @Test
    void testAuthorize() {
        when(request.getResourceAccesses()).thenReturn(Lists.newArrayList(ra1, ra2));
        when(ra1.getResource()).thenReturn(r1);
        when(ra1.getAction()).thenReturn(Action.Read);
        when(ra1.getDecoratedResources()).thenReturn(null);
        when(ra2.getResource()).thenReturn(r2);
        when(ra2.getAction()).thenReturn(Action.Write);
        when(ra2.getDecoratedResources()).thenReturn(null);
        when(resourceDecorator.decorate(r1)).thenReturn(Lists.newArrayList(r3, r4));
        when(resourceDecorator.decorate(r2)).thenReturn(Lists.newArrayList(r5));
        when(authorizer.authorize(ra1)).thenReturn(raa1);
        when(authorizer.authorize(ra2)).thenReturn(raa2);
        when(r1.getType()).thenReturn(ResourceType.COLUMN);
        when(r1.getValue()).thenReturn("column1");
        when(r2.getType()).thenReturn(ResourceType.HDFS);
        when(r2.getValue()).thenReturn("hdfs://localhost:8020/data");

        AuthResponse response = service.authorize(request);

        assertNotNull(response);
        assertEquals(Lists.newArrayList(raa1, raa2), response.getResults());
        verify(resourceDecorator).decorate(r1);
        verify(ra1).setDecoratedResources(eq(Lists.newArrayList(r3, r4)));
        verify(authorizer).authorize(ra1);
        verify(resourceDecorator).decorate(r2);
        verify(ra2).setDecoratedResources(eq(Lists.newArrayList(r5)));
        verify(authorizer).authorize(ra2);
    }
}
