package com.qwt.message;

import com.qwt.message.consumer.consumer.MessageConsumerRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class MessageConsumerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MessageConsumerApplication.class, args);
        // entry point of running message consumer
        MessageConsumerRunner consumerRunner = context.getBean(MessageConsumerRunner.class);
        consumerRunner.run();
    }
}
