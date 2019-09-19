package com.jq.api.service;

import com.jq.api.domain.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface HelloService {

    @RequestMapping("/hello")
    //必须加value否则报错
    String hello(@RequestParam(value = "name") String name);
    @RequestMapping("feign1")
    public String feign1(@RequestParam("name") String name) ;
    @RequestMapping("feign2")
    public User feign2(@RequestHeader("name") String name, @RequestHeader("age") Integer age);
    @RequestMapping("feign3")
    public String feign3(@RequestBody User user);

}
