package com.qwt.message.consumer.hystrix;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties("hystrix")
public class HystrixProperties {

    private Map<String, String> timeout;
    private Map<String, String> threadpool;

    public int getHystrixTimeout(String commandName) {
        return Integer.parseInt(Optional.ofNullable(timeout.get(commandName))
                .orElse(Optional.ofNullable(timeout.get("default"))
                        .orElse("5000")
                ));
    }

    public int getThreadPoolSize(String commandName) {
        return Integer.parseInt(Optional.ofNullable(threadpool.get(commandName))
                .orElse(Optional.ofNullable(threadpool.get("default"))
                        .orElse("50")
                ));
    }

    public Map<String, String> getTimeout() {
        return timeout;
    }

    public Map<String, String> getThreadpool() {
        return threadpool;
    }

    public void setTimeout(Map<String, String> timeout) {
        this.timeout = timeout;
    }

    public void setThreadpool(Map<String, String> threadpool) {
        this.threadpool = threadpool;
    }
}
