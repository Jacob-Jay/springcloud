package com.jq.feign.error;

import com.jq.feign.domain.User;
import com.jq.feign.service.HelloService;
import org.springframework.stereotype.Component;

@Component
public class HytrixError implements HelloService {

    @Override
    public String hello(String name) {
        return "sorry ............... feign  ..."+name;
    }

    @Override
    public String feign1(String name) {
        return "sorry ............... feign  ..."+name;
    }

    @Override
    public User feign2(String name, Integer age) {
        return null;
    }

    @Override
    public String feign3(User user) {
        return "sorry ............... feign  ..."+user.getName();
    }
}
