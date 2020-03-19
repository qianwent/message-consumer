package com.qwt.message.consumer.processor.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class ErrorLogWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogWriter.class);

    private KafkaProducer<String, String> kafkaProducer;
    private String errorTopicName;
    private ObjectMapper objectMapper;

    public ErrorLogWriter(KafkaProducer<String, String> kafkaProducer, String errorTopicName, ObjectMapper objectMapper) {
        this.kafkaProducer = kafkaProducer;
        this.errorTopicName = errorTopicName;
        this.objectMapper = objectMapper;
    }

    public void handleError(String message, Exception exception) throws Exception {
        ErrorTopicMessage errorTopicMessage = new ErrorTopicMessage(message, exception.getMessage());
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(errorTopicName, serializeToJson(errorTopicMessage));
        try {
            Future<RecordMetadata> errorPostCall = kafkaProducer.send(producerRecord);
            RecordMetadata recordMetadata = errorPostCall.get();
            LOGGER.info("Error message sent to kafka - " + recordMetadata.topic());
        } catch (Exception e) {
            LOGGER.error("Error in Writing to Error Queue", e);
            throw e;
        }
    }

    public String serializeToJson(ErrorTopicMessage errorTopicMessage) {
        try {
            return objectMapper.writeValueAsString(errorTopicMessage);
        } catch (JsonProcessingException e) {
            //cannot process..at least send back the message
            return errorTopicMessage.message;
        }
    }
}
