package com.qwt.message.consumer.processor.retry;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class RetryTimeManagerFactory {

    @Lookup
    public abstract RetryTimeManager create();
}
