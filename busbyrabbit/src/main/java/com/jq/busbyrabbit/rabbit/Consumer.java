package com.jq.busbyrabbit.rabbit;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-28 11:23
 */
@Component
@RabbitListener(queuesToDeclare = @Queue("hello"))
public class Consumer {
    public static final String CHANNEL =ChannelEnum.HELLO.getName();

    @RabbitHandler
    public void process(String hello) {
        System.out.println(hello);
    }
}
