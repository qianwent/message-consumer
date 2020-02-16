package com.qwt.message.consumer.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class MessageConsumerRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerRunner.class);

    private int numberOfConsumers;
    private long monitorPolling;
    private final MessageConsumerFactory messageConsumerFactory;
    private ExecutorService executor;
    private List<MessageConsumerThread> messageConsumerList = new ArrayList<>();

    @Autowired
    public MessageConsumerRunner(MessageConsumerFactory messageConsumerFactory) {
        // TODO: int numberOfConsumers, ExecutorService executor, cannot be used in constructor parameter, because no such bean found
        this.numberOfConsumers = 5;//TODO: use kafkaConfig.getConsumersCount()
        this.messageConsumerFactory = messageConsumerFactory;
        this.executor = Executors.newCachedThreadPool();
    }

    public void run() {
        IntStream.range(0, numberOfConsumers).forEach(i -> createMessageConsumer());
        // even Async, need to execute it, it's not automatic...
        monitor();
    }

    private void createMessageConsumer() {
        LOGGER.info("Creating message consumer...");
        MessageConsumer consumer = messageConsumerFactory.create();
        messageConsumerList.add(new MessageConsumerThread(executor.submit(consumer), consumer));
    }

    @Async
    public void monitor() {
        while (true) {
            // the purpose here is to check if thread is done with job, if yes, remove it and recreate
            try {
                List<MessageConsumerThread> done = messageConsumerList.stream().filter(i -> i.thread.isDone()).collect(Collectors.toList());
                done.stream().forEach(i -> {
//                    done.remove(i);
                    messageConsumerList.remove(i);
                    createMessageConsumer();
                });
                if (done.size() != 0) {

                }

                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MessageConsumerThread {

        Future<?> thread;
        MessageConsumer consumer;

        public MessageConsumerThread(Future<?> thread, MessageConsumer consumer) {
            this.thread = thread;
            this.consumer = consumer;
        }
    }
}
