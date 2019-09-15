package com.jq.ribbon.Controller.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019/9/14 16:09
 */

@RestController
public class GetRequest {
    @Autowired
    private RestTemplate restTemplate;

    @ResponseBody
    public String getHello() {
        String body = restTemplate.getForEntity(null, String.class).getBody();
        return body;
    }
}
