package com.qwt.message.consumer.consumer;

import com.qwt.message.consumer.config.KafkaConfig;
import com.qwt.message.consumer.processor.MessageProcessor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

@Component
@Scope("prototype")//TODO: I don't think prototype is necessary here. Different threads can still share the same bean, as the bean is not stateful...
public class MessageConsumer implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    private ForkJoinPool pool;
    private final KafkaConfig kafkaConfig;
    private final Consumer<String, String> consumer;
    private final List<String>topics;
    private MessageProcessor processor;

    @Autowired
    public MessageConsumer(KafkaConfig kafkaConfig, Consumer<String, String> consumer, MessageProcessor processor) {
        this.kafkaConfig = kafkaConfig;
        this.topics = Arrays.asList(kafkaConfig.getTopic());
        this.consumer = consumer;
        this.processor = processor;
        this.pool = new ForkJoinPool(kafkaConfig.getProcessorThreadPool());
    }

    @Override
    public void run() {
        LOGGER.info("<---START---> " + this.hashCode());

        try {
            RebalanceListener listener = new RebalanceListener(consumer);
            LOGGER.info("subscribing to " + topics);
            consumer.subscribe(topics, listener);
            ForkJoinTask task = null;

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(kafkaConfig.getTopicPolling());
                LOGGER.info("record is empty ? ------------------ " + records.isEmpty());

                if (!records.isEmpty()) {
                    LOGGER.info("record not empty, start to run the task");
                    task = pool.submit(new ProcessorRecursiveTask(processor, createRecordsList(records)));
                }

                if (shouldPause(task)) {
                    LOGGER.info("pause the consumer");
                    consumer.pause(listener.getPartitions());
                }

                if (isDoneProcessing(task)) {
                    LOGGER.info("processing is done, start to resume the consumer");
                    consumer.commitSync();
                    task = null;
                    consumer.resume(listener.getPartitions());
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            consumer.close();
        }
    }

    private boolean isDoneProcessing(ForkJoinTask task) {
        return task != null && task.isDone();
    }

    private boolean shouldPause(ForkJoinTask task) {
        return wasThereProcessingGoingOn(task) && !task.isDone() && !isConsumerPaused();
    }

    private boolean isConsumerPaused() {
        return !this.consumer.paused().isEmpty();
    }

    private boolean wasThereProcessingGoingOn(ForkJoinTask task) {
        return task != null;
    }

    public void shutdown() {
        LOGGER.info("Task shut down");
    }

    private List<ConsumerRecord> createRecordsList(ConsumerRecords<String, String> consumerRecords) {
        List<ConsumerRecord> recordValues = new ArrayList<>();

        consumerRecords.forEach( record -> recordValues.add(record));

        LOGGER.info("Looping record values now...");
        for (ConsumerRecord recordValue : recordValues) {
            LOGGER.info(recordValue.value().toString());
        }

        return recordValues;
    }
}
