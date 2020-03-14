package com.qwt.message.consumer.consumer;

import com.qwt.message.consumer.processor.MessageProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ProcessorRecursiveTask extends RecursiveAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorRecursiveTask.class);

    private MessageProcessor processor;
    private List<ConsumerRecord> records;

    public ProcessorRecursiveTask(MessageProcessor processor, List<ConsumerRecord> records) {
        this.processor = processor;
        this.records = records;
    }

    @Override
    protected void compute() {
        LOGGER.info("compute with records size: " + records.size());
        if (records.size() > 1) {
            ForkJoinTask.invokeAll(createTasks(records));
        } else {
            LOGGER.info("only one record: " + records.get(0).value());
            process(records.get(0));
        }

    }

    public Collection<ProcessorRecursiveTask> createTasks(List<ConsumerRecord> records) {
        Collection<ProcessorRecursiveTask> tasks = new ArrayList<>();

        records.forEach( record ->
                tasks.add(new ProcessorRecursiveTask(processor, Arrays.asList(record)))
        );

        return tasks;
    }

    public void process(ConsumerRecord record) {
        processor.process(record);
    }
}
