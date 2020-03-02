package com.qwt.message.consumer.config;

import com.qwt.message.consumer.processor.MessageProcessor;
import com.qwt.message.consumer.processor.Processor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Optional;
import java.util.Properties;

@Configuration
public class ConsumerApplicationConfig {

    @Bean("messageProcessor")
    public MessageProcessor<String> messageProcessor(
            @Qualifier("coreProcessor")
            Processor<ConsumerRecord, Optional<String>> coreProcessor
    ) {
        return new MessageProcessor<>(coreProcessor);
    }

    @Bean
    @Scope("prototype")
    public Consumer<String, String> consumer(Properties kafkaConsumerProperties) {
        return new KafkaConsumer<>(kafkaConsumerProperties);
    }

    @Bean("kafkaConsumerProperties")
    public Properties connectionProperties(KafkaConfig kafkaConfig){
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
}
