package com.jq.ribbon.Controller.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019/9/14 16:09
*/
@RestController
public class PostRequest {
    @Autowired
    private RestTemplate restTemplate;


    public void getHello() {
        URI sd = restTemplate.postForLocation(null, new String("sd"));

        System.out.println(sd);
    }
}