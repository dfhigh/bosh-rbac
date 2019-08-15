package com.bosh.rbac.component;

import com.bosh.rbac.context.RbacContext;
import com.bosh.rbac.context.RbacScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
@Configuration
public class RbacContextProcessor implements HandlerInterceptor {

    @Value("rbac.requestId.headerName")
    private String requestIdHeaderName;
    @Value("rbac.userId.headerName")
    private String userIdHeaderName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(requestIdHeaderName);
        if (StringUtils.isBlank(requestId)) requestId = UUID.randomUUID().toString();
        String userId = request.getHeader(userIdHeaderName);
        MDC.put("uId", userId);
        MDC.put("rId", requestId);
        RbacScope.setContext(new RbacContext(requestId, userId));
        log.debug("rbac context initialized");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav) {
        RbacContext context = RbacScope.clearContext();
        if (context != null) response.addHeader(requestIdHeaderName, context.getRequestId());
        log.debug("rbac context cleared");
        MDC.clear();
    }
}
