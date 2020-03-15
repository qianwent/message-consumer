package com.qwt.message.consumer.processor.retry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RetryTimeManager {

    private long currentTimeInterval;
    private long totalDuration;
    private long count;

    public RetryTimeManager(@Value("${recoverable-retry.min}") long currentTimeInterval) {
        this.currentTimeInterval = currentTimeInterval;
        totalDuration = 0;
        count = 0;
    }

    // TODO
    public long nextInterval() {
        count++;
        currentTimeInterval += 3000;
        return currentTimeInterval;
    }
}
