package com.jq.busbyrabbit.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-28 11:09
 */
@Component
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String content) {
       /* try {
            Message message = new Message(content.getBytes("utf-8"),new MessageProperties());
            rabbitTemplate.send(ChannelEnum.HELLO.getName(),message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        rabbitTemplate.convertAndSend(ChannelEnum.HELLO.getName(),content);
    }
}
