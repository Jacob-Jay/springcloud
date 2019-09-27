package com.jq.configclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-26 16:58
 */
@RefreshScope   //动态刷新配置
@RestController
@RequestMapping("configClient")
public class TestController {



    @Value("${age}")
    private int age;
    @Value("${name}")
    private String name;

    @RequestMapping("value")
    public String hello() {
        return name + "   " + age;
    }


    @Autowired
    private Environment environment;

    @RequestMapping("env")
    public String env() {
        return environment.getProperty("name") + "   " + environment.getProperty("age");
    }
}
