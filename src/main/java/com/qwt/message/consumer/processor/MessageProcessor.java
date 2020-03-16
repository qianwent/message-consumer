package com.qwt.message.consumer.processor;

import com.qwt.message.consumer.exception.RecoverableException;
import com.qwt.message.consumer.processor.retry.RetryTimeManager;
import com.qwt.message.consumer.processor.retry.RetryTimeManagerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

/**
 * 这个是每个线程执行的起始点，即总的processor，有着典型的重试retry机制，属于high availability的范畴
 * 1. coreProcessor包含对多个接口的操作，如果全部执行成功，则状态变为PROCESSED，无需retry
 * 2. 如果coreProcessor中任何一个步骤抛出RecoverableException，开始retry，但不是无限制的retry，
 *    当retry次数达到设置的上限之后，停止retry，并将当前message推送到kafka的retry error topic
 * 3. 如果coreProcessor中任何一个步骤抛出其他Exception，则直接handleError，不进行retry，
 *    并将当前message推送到error topic
 * 4. 以上两个步骤在将message推送回kafka的时候，依然有可能抛出异常，
 *    但这个是另外一个话题了-Kafka写入的数据如何保证不丢失，
 *    所以一旦有异常，再次进入while大循环retry
 */
public class MessageProcessor<D> implements Processor<ConsumerRecord<String, String>, Optional<D>> {

    private Processor<ConsumerRecord, Optional<D>> coreProcessor;
    private RetryTimeManagerFactory retryTimeManagerFactory;

    public MessageProcessor(Processor<ConsumerRecord, Optional<D>> coreProcessor, RetryTimeManagerFactory retryTimeManagerFactory) {
        this.coreProcessor = coreProcessor;
        this.retryTimeManagerFactory = retryTimeManagerFactory;
    }

    @Override
    public Optional<D> process(ConsumerRecord<String, String> source) {
        // introduce logic of retry
        Optional<D> processedRecord = Optional.empty();
        RetryTimeManager retryTimeManager = retryTimeManagerFactory.create();
        State processed = State.NOT_PROCESSED;
        while (processed != State.PROCESSED) {
            try {
                processedRecord = coreProcessor.process(source);
                processed = State.PROCESSED;
            } catch (RecoverableException ex) {
                /**
                 * note, even for RecoverableException, retry shouldn't be unlimited
                 * after certain times of retry, need to stop and write message to retry error topic
                 */
                processed = sleepUntilRetryThreshold(retryTimeManager,source, ex);
            } catch (Exception ex) {
                boolean errorProcessed = handleError(source, ex);
                if (!errorProcessed) {
                    sleep(retryTimeManager.nextInterval());
                    processed = State.UNKNOWN_ERROR_NOT_PROCESSED;
                } else {
                    processed = State.PROCESSED;
                }
            }
        }
        return processedRecord;
    }

    private boolean handleError(ConsumerRecord<String, String> source, Exception ex) {
        try {
            // TODO unrecoverable error, write to error topic
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private State sleepUntilRetryThreshold(
            RetryTimeManager retryTimeManager, ConsumerRecord<String, String> source,
            Exception ex
    ) {
        sleep(retryTimeManager.nextInterval());
        boolean retryLimitErrorProcessed = handleRetryLimitError(source, ex);
        return (retryLimitErrorProcessed)? State.PROCESSED : State.THRESHOLD_ERROR_NOT_PROCESSED;
    }

    private boolean handleRetryLimitError(ConsumerRecord<String, String> source, Exception ex) {
        try {
            // TODO retry limit error, write to retry error topic
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void sleep(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            // TODO
        }
    }

    public enum State {
        PROCESSED,
        NOT_PROCESSED,
        UNKNOWN_ERROR_NOT_PROCESSED,
        THRESHOLD_ERROR_NOT_PROCESSED,
        RETRY_PROCESS
    }
}
