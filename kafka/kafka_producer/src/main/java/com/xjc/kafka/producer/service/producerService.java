package com.xjc.kafka.producer.service;

import java.util.Map;

public interface producerService {

    void syncSend(Object key, Map<Object, Object> map, Long timeOut);

}
