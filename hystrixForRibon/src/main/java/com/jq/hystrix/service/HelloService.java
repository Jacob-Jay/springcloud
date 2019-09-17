package com.jq.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class HelloService {

    @Autowired
    private RestTemplate restTemplate;

    //同步执行 commond.execute
    @HystrixCommand(fallbackMethod = "helloError")
    public String hello(String name) {
        String forObject = restTemplate.getForObject("http://CLIENT-HI/hello?name=" + name, String.class);
        return forObject;
    }

    /**
     * 异步执行
     * Future<String>s = commond.queue();
     * doOtherThing
     * s.get();获取结果
     * @param name
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloError",ignoreExceptions = RuntimeException.class)
    public String helloAsync(String name) {

        Future<String> result = new AsyncResult<String>() {
            @Override
            public String invoke() {
                return restTemplate.getForObject("http://CLIENT-HI/hello?name=" + name, String.class);
            }

            @Override
            public String get() throws UnsupportedOperationException {
                return invoke();
            }
        };

        System.out.println("sdasda");
        String s = null;
        try {
            s = result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return s;

    }
    public String helloError(String name,Throwable throwable) {
        return "sorry......" + name;
    }
}
