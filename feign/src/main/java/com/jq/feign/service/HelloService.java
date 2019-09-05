package com.jq.feign.service;

import com.jq.feign.error.HytrixError;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "CLIENT-HI",fallback = HytrixError.class)
@Service
public interface HelloService {

    @RequestMapping("/hello")
    //必须加value否则报错
    String hello(@RequestParam(value = "name") String name);
}
