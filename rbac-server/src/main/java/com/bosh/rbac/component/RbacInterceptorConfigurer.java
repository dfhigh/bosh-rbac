package com.bosh.rbac.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class RbacInterceptorConfigurer implements WebMvcConfigurer {

    @Autowired
    private RbacContextProcessor contextProcessor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextProcessor);
    }
}
