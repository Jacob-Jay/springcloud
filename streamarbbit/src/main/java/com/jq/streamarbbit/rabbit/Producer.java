package com.jq.streamarbbit.rabbit;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author Jiangqing
 * @version 1.0
 * @since 2019-09-30 16:59
 */
public interface Producer {

    /**
     * Name of the output channel.
     */
    String OUTPUT = "output";

    /**
     * @return output channel
     */
    @Output(Producer.OUTPUT)
//    @Output(Sink.INPUT)
    MessageChannel output();
}
