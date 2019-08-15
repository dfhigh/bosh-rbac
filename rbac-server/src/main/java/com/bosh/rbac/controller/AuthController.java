package com.bosh.rbac.controller;

import com.bosh.rbac.auth.model.AuthRequest;
import com.bosh.rbac.auth.model.AuthResponse;
import com.bosh.rbac.component.RestAPIWrapper;
import com.bosh.rbac.rest.model.Response;
import com.bosh.rbac.service.AuthorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "auth APIs", description = "APIs for authorizing resources accesses")
public class AuthController {

    private final RestAPIWrapper wrapper;
    private final AuthorizationService authorizationService;

    @Autowired
    public AuthController(final RestAPIWrapper wrapper, final AuthorizationService authorizationService) {
        this.wrapper = wrapper;
        this.authorizationService = authorizationService;
    }

    @PostMapping(value = "/auth", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "authorize resources accesses", produces = "application/json; charset=utf-8")
    public ResponseEntity<Response<AuthResponse>> authorize(
            @ApiParam(required = true, value = "resources accesses body")
            @RequestBody AuthRequest authRequest
    ) {
        return wrapper.wrap(() -> authorizationService.authorize(authRequest));
    }
}
