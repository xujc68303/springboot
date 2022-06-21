package com.xjc.kafka.producer.service;

import java.util.Map;

public interface ProducerService {

    boolean createTopic(String topicName, int partition, short replication);

    void syncSend(String topic, String key, Integer partition, Map<String, Object> map, Long timeOut);

    void asyncSend(String topic, String key, Map<String, Object> map);

}
