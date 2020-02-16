package com.qwt.message.consumer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @Override
    public void run() {
        LOGGER.info("<---START--->");
    }
}
