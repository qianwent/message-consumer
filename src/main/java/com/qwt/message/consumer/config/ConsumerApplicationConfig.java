package com.qwt.message.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qwt.message.consumer.http.RestOperationsFactory;
import com.qwt.message.consumer.processor.MessageProcessor;
import com.qwt.message.consumer.processor.Processor;
import com.qwt.message.consumer.processor.exception.ErrorLogWriter;
import com.qwt.message.consumer.processor.retry.RetryTimeManagerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestOperations;

import java.util.Optional;
import java.util.Properties;

@Configuration
public class ConsumerApplicationConfig {

    @Bean("messageProcessor")
    public MessageProcessor<String> messageProcessor(
            @Qualifier("coreProcessor")
                    Processor<ConsumerRecord, Optional<String>> coreProcessor,
            RetryTimeManagerFactory retryTimeManagerFactory,
            @Qualifier("errorLogWriter") ErrorLogWriter errorLogWriter) {
        return new MessageProcessor<>(coreProcessor, retryTimeManagerFactory, errorLogWriter);
    }

    @Bean
    @Scope("prototype")
    public Consumer<String, String> consumer(Properties kafkaConsumerProperties) {
        return new KafkaConsumer<>(kafkaConsumerProperties);
    }

    @Bean("kafkaConsumerProperties")
    public Properties connectionProperties(KafkaConfig kafkaConfig) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConfig.getBootstrapServers());
        props.put("request.timeout.ms", kafkaConfig.getRequestTimeout());
        props.put("session.timeout.ms", kafkaConfig.getSessionTimeout());
        props.put("group.id", kafkaConfig.getGroupId());
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        //how many max records to pull in one poll
        props.put("max.poll.records", kafkaConfig.getMaxPollRecords());
        //how many ms interval to poll within without timeout
        props.put("max.poll.interval.ms", kafkaConfig.getMaxPollInterval());

//        addKafkaSecurity(kafkaConfig, props);

        return props;
    }

    @Bean("addValueAPIRestOperations")
    public RestOperations addValueAPIRestOperations(RestOperationsFactory restOperationsFactory) {
        return restOperationsFactory.createRestOperations("addValue");
    }

    @Bean("errorLogWriter")
    public ErrorLogWriter unrecoverableErrorLogWriter(
            @Qualifier("errorTopicMessageKafkaProducer") KafkaProducer<String, String> kafkaProducer,
            @Value("${kafka.error-topic}") String errorTopicName,
            ObjectMapper objectMapper) {
        return new ErrorLogWriter(kafkaProducer, errorTopicName, objectMapper);
    }

    @Bean("errorTopicMessageKafkaProducer")
    public KafkaProducer<String, String> errorTopicMessageKafkaProducer(
            @Qualifier("kafkaErrorProducerProperties") Properties properties) {
        return new KafkaProducer<>(properties);
    }

    @Bean("kafkaErrorProducerProperties")
    public Properties errorProducerProperties(KafkaConfig kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
}
