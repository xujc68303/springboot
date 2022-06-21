package com.xjc.kafka.producer.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    private final static Long TIME_OUT = 3000L;

    @Override
    public boolean createTopic(String topicName, int partition, short replication) {
        NewTopic newTopic = new NewTopic(topicName, partition, replication);
        CreateTopicsOptions createTopicsOptions = new CreateTopicsOptions();
        createTopicsOptions.validateOnly(true);
        kafkaAdminClient.createTopics(Collections.singleton(newTopic), createTopicsOptions);
        return true;
    }

    @Override
    public void syncSend(String topic, String key, Integer partition, Map<String, Object> map, Long timeOut) {
        map.put("CREATE_DATE", LocalDateTime.now());
        timeOut = timeOut == null ? TIME_OUT : timeOut;
        ListenableFuture<SendResult<Object, Object>> send = kafkaTemplate.send(topic, partition, key, map.toString());
        try {
            SendResult<Object, Object> sendResult = send.get(timeOut, TimeUnit.MILLISECONDS);
            send.addCallback(successCallback -> {
                RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                log.warn("kafka Producer发送消息成功！ topic=" + recordMetadata.topic() + ", offset=" + recordMetadata.offset()
                        + ", partition=" + recordMetadata.partition());
            }, failureCallback -> log.error("kafka Producer发送消息异常！ sendResult=" + failureCallback.getMessage()));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            log.error("同步消息发送超时");
        }
    }

    @Override
    public void asyncSend(String topic, String key, Map<String, Object> map) {
        map.put("CREATE_DATE", LocalDateTime.now());
        ListenableFuture<SendResult<Object, Object>> send = kafkaTemplate.send(topic, key, JSON.toJSONString(map));
        send.addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("kafka producer async service error={}", throwable);
            }
            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("kafka producer service success topic={}", recordMetadata.topic());
            }
        });
    }
}
