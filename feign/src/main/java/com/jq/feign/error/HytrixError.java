package com.jq.feign.error;

import com.jq.feign.service.HelloService;
import org.springframework.stereotype.Component;

@Component
public class HytrixError implements HelloService {

    @Override
    public String hello(String name) {
        return "sorry ............... feign  ..."+name;
    }
}
