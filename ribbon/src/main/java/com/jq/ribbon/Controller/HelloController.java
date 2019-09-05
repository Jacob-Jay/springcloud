package com.jq.ribbon.Controller;

import com.jq.ribbon.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-05 18:53
 */
@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("hello")
    public String hello(@RequestParam String name) {
        return helloService.hiService(name);
    }
}
