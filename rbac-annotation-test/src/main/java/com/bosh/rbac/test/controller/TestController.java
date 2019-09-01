package com.bosh.rbac.test.controller;

import com.bosh.rbac.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(final TestService testService) {
        this.testService = testService;
    }

    @GetMapping(value = "/test1", produces = "text/plain")
    public String test1() {
        return testService.test1();
    }

    @GetMapping(value = "/test2/{name}", produces = "text/plain")
    public String test2(@PathVariable String name) {
        return testService.test2(name);
    }

    @GetMapping(value = "/test3/{name}", produces = "text/plain")
    public String test3(@PathVariable String name) {
        return testService.test3(name);
    }
}
