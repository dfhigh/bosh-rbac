package com.bosh.rbac.component;

import com.bosh.rbac.context.RbacContext;
import com.bosh.rbac.context.RbacScope;
import com.bosh.rbac.model.User;
import com.bosh.rbac.rest.model.Response;
import com.bosh.rbac.rest.model.Status;
import com.bosh.rbac.service.EntityReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mib.rest.exception.BadRequestException;
import org.mib.rest.exception.ForbiddenException;
import org.mib.rest.exception.ResourceNotFoundException;
import org.mib.rest.exception.UnauthorizedException;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static com.bosh.rbac.component.RestAPIWrapper.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("ut")
class RestAPIWrapperTest {

    @Mock
    private Object object;
    @Mock
    private User user;
    @Mock
    private RbacContext context;
    @MockBean
    private EntityReadService service;
    @Autowired
    private RestAPIWrapper wrapper;

    @BeforeEach
    void setup() {
        RbacScope.setContext(context);
        when(context.getUser()).thenReturn(user);
    }

    @Test
    void testNoUserInContext() {
        when(context.getUser()).thenReturn(null);

        when(context.getUserId()).thenReturn(null);
        validateResponseEntity(wrapper.wrap(() -> {}), HttpStatus.UNAUTHORIZED, Status.UNAUTHENTICATED, null);

        when(context.getUserId()).thenReturn("");
        validateResponseEntity(wrapper.wrap(() -> {}), HttpStatus.UNAUTHORIZED, Status.UNAUTHENTICATED, null);

        when(context.getUserId()).thenReturn(" ");
        validateResponseEntity(wrapper.wrap(() -> {}), HttpStatus.UNAUTHORIZED, Status.UNAUTHENTICATED, null);

        String userId = "userId";
        when(context.getUserId()).thenReturn(userId);
        when(service.getUser(userId)).thenReturn(null);
        validateResponseEntity(wrapper.wrap(() -> {}), HttpStatus.UNAUTHORIZED, Status.UNAUTHENTICATED, null);
    }

    @Test
    void test200() {
        validateResponseEntity(wrapper.wrap(() -> object), HttpStatus.OK, Status.OK, object);
        validateResponseEntity(wrapper.wrap(() -> {}), HttpStatus.OK, Status.OK, EMPTY);
    }

    @Test
    void test400() {
        validateResponseEntity(wrapper.wrap(() -> {
            throw new BadRequestException("mocked");
        }), HttpStatus.BAD_REQUEST, Status.BAD_REQUEST, null);
    }

    @Test
    void test401() {
        validateResponseEntity(wrapper.wrap(() -> {
            throw new UnauthorizedException("mocked");
        }), HttpStatus.UNAUTHORIZED, Status.UNAUTHENTICATED, null);
    }

    @Test
    void test403() {
        validateResponseEntity(wrapper.wrap(() -> {
            throw new ForbiddenException("mocked");
        }), HttpStatus.FORBIDDEN, Status.FORBIDDEN, null);
    }

    @Test
    void test404() {
        validateResponseEntity(wrapper.wrap(() -> {
            throw new ResourceNotFoundException("mocked");
        }), HttpStatus.NOT_FOUND, Status.NOT_FOUND, null);
    }

    @Test
    void test500() {
        validateResponseEntity(wrapper.wrap(() -> {
            throw new RuntimeException("mocked");
        }), HttpStatus.INTERNAL_SERVER_ERROR, Status.INTERNAL_ERROR, null);
    }

    private <T> void validateResponseEntity(ResponseEntity<Response<T>> re, HttpStatus httpStatus, Status status, T body) {
        assertNotNull(re);
        assertEquals(httpStatus, re.getStatusCode());
        Response response = re.getBody();
        assertNotNull(response);
        assertEquals(status, response.getStatus());
        assertEquals(body, response.getBody());
    }
}
