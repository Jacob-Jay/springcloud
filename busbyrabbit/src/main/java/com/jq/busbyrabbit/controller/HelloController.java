package com.jq.busbyrabbit.controller;

import com.jq.busbyrabbit.rabbit.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-28 11:21
 */
@RestController
@RequestMapping("hello")
public class HelloController {

    @Autowired
    private Producer producer;

    @RequestMapping("send")
    public String send(@RequestParam String content) {
        producer.send(content);
        return "ok";
    }
}
