package com.qwt.message.consumer.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

public class RebalanceListener implements ConsumerRebalanceListener {

    private Collection<TopicPartition> partitions;
    private Consumer consumer;

    public RebalanceListener(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        this.partitions = partitions;
    }

    public Collection<TopicPartition> getPartitions() {
        return partitions;
    }
}
