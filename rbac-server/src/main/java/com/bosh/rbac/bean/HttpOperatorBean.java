package com.bosh.rbac.bean;

import org.mib.rest.client.HttpOperator;
import org.mib.rest.client.SyncHttpOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpOperatorBean {

    @Bean
    public HttpOperator httpOperator() {
        return new SyncHttpOperator();
    }
}
