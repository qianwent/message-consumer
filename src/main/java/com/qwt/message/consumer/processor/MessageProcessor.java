package com.qwt.message.consumer.processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

public class MessageProcessor<D> implements Processor<ConsumerRecord<String, String>, Optional<D>> {

    private Processor<ConsumerRecord, Optional<D>> coreProcessor;

    public MessageProcessor(Processor<ConsumerRecord, Optional<D>> coreProcessor) {
        this.coreProcessor = coreProcessor;
    }

    @Override
    public Optional<D> process(ConsumerRecord<String, String> source) {
        return coreProcessor.process(source);
    }
}
