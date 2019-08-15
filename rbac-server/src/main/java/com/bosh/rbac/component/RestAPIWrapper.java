package com.bosh.rbac.component;

import com.bosh.rbac.context.RbacScope;
import com.bosh.rbac.model.User;
import com.bosh.rbac.rest.model.Response;
import com.bosh.rbac.rest.model.Status;
import com.bosh.rbac.service.EntityReadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mib.rest.exception.BadRequestException;
import org.mib.rest.exception.ForbiddenException;
import org.mib.rest.exception.ResourceNotFoundException;
import org.mib.rest.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
public class RestAPIWrapper {

    static final Object EMPTY = new Object();

    private final EntityReadService entityReadService;

    @Autowired
    public RestAPIWrapper(final EntityReadService entityReadService) {
        this.entityReadService = entityReadService;
    }

    public ResponseEntity<Response<Object>> wrap(Runnable runnable) {
        return wrap(() -> {
            runnable.run();
            return EMPTY;
        });
    }

    public <T> ResponseEntity<Response<T>> wrap(Supplier<T> supplier) {
        Response<T> response = new Response<>();
        try {
            ensureAuthentication();
            response.setBody(supplier.get());
            response.setStatus(Status.OK);
            return ResponseEntity.ok(response);
        } catch (BadRequestException | IllegalArgumentException e) {
            response.setStatus(Status.BAD_REQUEST);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UnauthorizedException e) {
            response.setStatus(Status.UNAUTHENTICATED);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (ForbiddenException e) {
            response.setStatus(Status.FORBIDDEN);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (ResourceNotFoundException e) {
            response.setStatus(Status.NOT_FOUND);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("caught exception", e);
            response.setStatus(Status.INTERNAL_ERROR);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void ensureAuthentication() {
        if (RbacScope.getUser() != null) return;
        String userId = RbacScope.getUserId();
        if (StringUtils.isBlank(userId)) throw new UnauthorizedException("no user in context");
        User user = entityReadService.getUser(userId);
        if (user == null) throw new UnauthorizedException("unrecognized user " + userId);
        RbacScope.setUser(user);
    }
}
