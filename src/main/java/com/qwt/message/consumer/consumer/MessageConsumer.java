package com.qwt.message.consumer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")//TODO: I don't think prototype is necessary here. Different threads can still share the same bean, as the bean is not stateful...
public class MessageConsumer implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @Override
    public void run() {
        LOGGER.info("<---START---> " + this.hashCode());
    }

    public void shutdown() {
        LOGGER.info("Task shut down");
    }
}
