package com.jq.streamarbbit.rabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-30 17:01
 */
@EnableBinding(Producer.class)
@RestController
@RequestMapping("stream")
public class SendController {
    @Autowired
    private Producer producer;

    @Autowired
    private MessageChannel output;
    @RequestMapping("send")
    public String send(String content) {

//        producer.output().send(MessageBuilder.withPayload(content).build());
        output.send(MessageBuilder.withPayload(content).build());
        return "ok";
    }
}
