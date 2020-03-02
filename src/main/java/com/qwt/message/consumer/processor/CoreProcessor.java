package com.qwt.message.consumer.processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoreProcessor implements Processor<ConsumerRecord<String, String>, Optional<String>> {

    @Override
    public Optional<String> process(ConsumerRecord<String, String> source) {
        return Optional.ofNullable(source).map(x->x.value());
    }
}
