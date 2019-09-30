package com.jq.streamarbbit.rabbit;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-30 15:24
 */
@EnableBinding({Sink.class})
public class Receiver {


    @StreamListener(Producer.OUTPUT)
    public void receive(Object obj) {
        System.out.println("receive "+ obj);
    }
}
