package com.qwt.message.consumer.consumer;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class MessageConsumerFactory {

    @Lookup
    protected abstract MessageConsumer create();
}
