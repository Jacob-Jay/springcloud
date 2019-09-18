package com.jq.eurekaClient.controller;

import com.jq.eurekaClient.domian.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

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

        /*int i = new Random().nextInt(3000);
        System.out.println(i);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return "hello  "+name+"i'm from "+port;
    }


    @RequestMapping("feign1")
    public String feign1(@RequestParam String name) {
        return "feign  "+name+"i'm from "+port;
    }
    @RequestMapping("feign2")
    public User feign2(@RequestHeader String name, @RequestHeader Integer age) {
        return  new User(name,age);
    }
    @RequestMapping("feign3")
    public String feign3(@RequestBody User user) {
        return "feign  "+user.getName()+"i'm from "+port;
    }
}
