package com.qwt.message.consumer.consumer;

import com.qwt.message.consumer.config.KafkaConfig;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class MessageConsumerRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerRunner.class);

    private int numberOfConsumers;
    private long monitorPolling;
    private final MessageConsumerFactory messageConsumerFactory;
    private ExecutorService executor;
    private List<MessageConsumerThread> consumerThreads = new ArrayList<>();

    @Autowired
    public MessageConsumerRunner(KafkaConfig kafkaConfig, MessageConsumerFactory messageConsumerFactory) {
        // TODO: int numberOfConsumers, ExecutorService executor, cannot be used in constructor parameter, because no such bean found
        /**
         * note: how many consumer threads are needed? Based on calculation.
         * 估算主题的吞吐量T和消费者的吞吐量S，假设每秒从topic上写入和读取1G数据，而每个consumer线程每秒处理50M的数据
         * 那么至少需要20个partition，那么同样用20个consumer同时读取这些分区，从而达到每秒1G的吞吐量。
         * 这是粗略估算，实际上需要测试调优。
         */
        this.numberOfConsumers = kafkaConfig.getConsumersCount();
        this.messageConsumerFactory = messageConsumerFactory;
//        this.executor = Executors.newCachedThreadPool();
        // normally don't use newCachedThreadPool, because it would keep creating new threads if other threads are currently in use,
        // which might cause Exception in thread "main" java.lang.OutOfMemoryError: unable to create new native thread
        this.executor = Executors.newFixedThreadPool(numberOfConsumers);
        this.manageShutdown();
    }

    public void run() {
        IntStream.range(0, numberOfConsumers).forEach(i -> createMessageConsumer());
        // even Async, need to execute it, it's not automatic...
        monitor();
    }

    private void createMessageConsumer() {
        LOGGER.info("Creating message consumer...");
        MessageConsumer consumer = messageConsumerFactory.create();
        consumerThreads.add(new MessageConsumerThread(executor.submit(consumer), consumer));
    }

    @Async
    public void monitor() {
        while (true) {
            // the purpose here is to check if thread is done with job, if yes, remove it and recreate
            try {
                List<MessageConsumerThread> done = consumerThreads.stream().filter(i -> i.thread.isDone()).collect(Collectors.toList());
                done.stream().forEach(i -> {
//                    done.remove(i);
                    LOGGER.info("current thread done, remove thread - " + i + " and recreate thread");
                    consumerThreads.remove(i);
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

    /**
     * This is to gracefully shutdown JVM, which means before JVM got killed, the job of the threads would be finished
     */
    private void manageShutdown() {
        LOGGER.info("manage shut down");
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run () {
                LOGGER.info("consumerThreads: " + consumerThreads.size());
                for (MessageConsumerThread consumerThread : consumerThreads) {
                    consumerThread.consumer.shutdown();
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
