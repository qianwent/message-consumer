package com.qwt.message;

import com.qwt.message.consumer.consumer.MessageConsumerRunner;
import com.qwt.message.beanscope.SingletonBean;
import com.qwt.message.beanscope.SingletonBean_V2;
import com.qwt.message.beanscope.SingletonBean_V3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class MessageConsumerApplication {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MessageConsumerApplication.class, args);
        // entry point of running message consumer
        MessageConsumerRunner consumerRunner = context.getBean(MessageConsumerRunner.class);
        consumerRunner.run();
    }

    @Bean
    public CommandLineRunner test(final SingletonBean bean) {
        return (args)-> {
            logger.info("test singleton bean and prototype bean");
            int i =0;
            while(i<3) {
                i++;
                bean.print();
            }
        };
    }

    @Bean
    public CommandLineRunner test2(final SingletonBean_V2 bean) {
        return (args)-> {
            logger.info("test2 singleton bean and prototype bean");
            int i =0;
            while(i<3) {
                i++;
                bean.print();
            }
        };
    }

    @Bean
    public CommandLineRunner test3(final SingletonBean_V3 bean) {
        return (args)-> {
            logger.info("test3 singleton bean and prototype bean");
            int i =0;
            while(i<3) {
                i++;
                bean.print();
            }
        };
    }
}
