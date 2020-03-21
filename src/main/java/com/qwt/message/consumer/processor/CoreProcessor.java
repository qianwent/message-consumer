package com.qwt.message.consumer.processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoreProcessor implements Processor<ConsumerRecord<String, String>, Optional<String>> {

    private DBWriter dbWriter;
    private DWWriter dwWriter;

    @Autowired
    public CoreProcessor(DBWriter dbWriter,
                         DWWriter dwWriter) {
        this.dbWriter = dbWriter;
        this.dwWriter = dwWriter;
    }

    @Override
    public Optional<String> process(ConsumerRecord<String, String> source) {
        return Optional.ofNullable(source).map(x -> dbWriter.process(x.value()))
                .map(x -> dwWriter.process(x));
    }
}
