package com.qwt.message.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("kafka")
public class KafkaConfig {

    private String bootstrapServers;
    private String groupId;
    private String topic;
    private Long topicPolling;
    private int consumersCount;
    private int maxPollInterval;
    private int maxPollRecords;
    private int processorThreadPool;
    private int requestTimeout;
    private int sessionTimeout;
    private String user;
    private String password;

    public int getProcessorThreadPool() {
        return processorThreadPool;
    }

    public void setProcessorThreadPool(int processorThreadPool) {
        this.processorThreadPool = processorThreadPool;
    }

    public int getMaxPollInterval() {
        return maxPollInterval;
    }

    public void setMaxPollInterval(int maxPollInterval) {
        this.maxPollInterval = maxPollInterval;
    }

    public int getMaxPollRecords() {
        return maxPollRecords;
    }

    public void setMaxPollRecords(int maxPollRecords) {
        this.maxPollRecords = maxPollRecords;
    }

    public long getMonitorPolling() {
        return monitorPolling;
    }

    public void setMonitorPolling(long monitorPolling) {
        this.monitorPolling = monitorPolling;
    }

    private long monitorPolling;

    public int getConsumersCount() {
        return consumersCount;
    }

    public void setConsumersCount(int consumersCount) {
        this.consumersCount = consumersCount;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getTopicPolling() {
        return topicPolling;
    }

    public void setTopicPolling(Long topicPolling) {
        this.topicPolling = topicPolling;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}
