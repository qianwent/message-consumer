package com.qwt.message.consumer.processor;

import com.qwt.message.consumer.exception.RecoverableException;
import com.qwt.message.consumer.processor.retry.RetryTimeManager;
import com.qwt.message.consumer.processor.retry.RetryTimeManagerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

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
                processed = sleepUntilRetryThreshold(source, ex);

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
        // TODO unrecoverable error, so write to error topic
        return true;
    }

    private State sleepUntilRetryThreshold(
            ConsumerRecord<String, String> source,
            Exception ex
    ) {
        sleep(500);
        return State.PROCESSED;
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
