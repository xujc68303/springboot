package com.xjc.kafka.producer.service;

import java.util.Map;

public interface ProducerService {

    boolean createTopic(String topicName, int partition, short replication);

    void syncSend(Object key, Map<Object, Object> map, Long timeOut);

}
