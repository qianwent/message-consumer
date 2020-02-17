package com.qwt.message.consumer.consumer;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class MessageConsumerFactory {

    /**
     * https://www.jianshu.com/p/fc574881e3a2
     */
    @Lookup
    protected abstract MessageConsumer create();
}
