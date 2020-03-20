package com.qwt.message.consumer.hystrix;

import com.netflix.config.ConfigurationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class HystrixInitializer {

    @Autowired
    HystrixProperties hystrixConfig;

    @Autowired
    public HystrixInitializer(HystrixProperties hystrixConfig) {
        this.hystrixConfig = hystrixConfig;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void contextRefreshedEvent() {
        setProperty(hystrixConfig, "AddValueCommand");
//        setProperty(hystrixConfig, "DWWriteCommand");
    }

    private void setProperty(HystrixProperties hystrixConfig, String commandName) {
        ConfigurationManager.getConfigInstance().setProperty(
                "hystrix.command." + commandName + ".execution.isolation.thread.timeoutInMilliseconds", hystrixConfig.getHystrixTimeout(commandName)
        );

        ConfigurationManager.getConfigInstance().setProperty(
                "hystrix.threadpool." + commandName + ".coreSize", hystrixConfig.getThreadPoolSize(commandName)
        );

    }
}
