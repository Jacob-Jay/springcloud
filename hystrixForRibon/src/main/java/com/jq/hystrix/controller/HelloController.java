package com.jq.hystrix.controller;

import com.jq.hystrix.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("hello")
    public String hello(@RequestParam String name){
        return helloService.hello(name);
    }
    @RequestMapping("helloAsync")
    public String helloAsync(@RequestParam String name){
        return helloService.helloAsync(name);
    }

}
