package com.jq.feign.controller;

import com.jq.feign.domain.User;

import com.jq.feign.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("hello")
    public String hello(@RequestParam String name) {
        return helloService.hello(name);
    }

    @RequestMapping("feign1")
    public String feign1(@RequestParam String name) {
        return helloService.feign1(name);
    }
    @RequestMapping("feign2")
    public User feign2(@RequestParam String name, @RequestParam Integer age) {
        return  helloService.feign2(name,age);
    }
    @RequestMapping("feign3")
    public String feign3(User user) {
        return helloService.feign3(user);
    }
}
