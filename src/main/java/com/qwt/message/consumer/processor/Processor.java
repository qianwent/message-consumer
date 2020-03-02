package com.qwt.message.consumer.processor;

public interface Processor<S, D> {

    D process(S source);
}
