package com.jq.ribbon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-05 18:54
 */
@Service
public class HelloService {



        @Autowired
        RestTemplate restTemplate;

        public String hiService(String name) {
            return restTemplate.getForObject("http://CLIENT-HI/hello?name="+name,String.class);
        }




}
