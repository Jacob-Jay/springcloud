package com.jq.eurekaClient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-05 18:10
 */
@RestController
public class HelloController {


    @Value("${server.port}")
    private String port;
    @RequestMapping("hello")
    public String hello(@RequestParam(value = "name",defaultValue = "jq")String name) {
        return "hello  "+name+"i'm from "+port;
    }
}
