package com.xjc.kafka.producer.service;

import com.xjc.kafka.producer.config.KakfaTopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class ProducerServiceImpl implements producerService {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private static Long TIME_OUT = 3000L;

    @Override
    public void syncSend(Object key, Map<Object, Object> map, Long timeOut) {
        map.put("CREATE_DATE", LocalDateTime.now());
        timeOut = timeOut == null ? TIME_OUT : timeOut;
        ListenableFuture<SendResult<Object, Object>> send = kafkaTemplate.send(KakfaTopicConfig.TOPIC, key, map.toString());
        try {
            SendResult<Object, Object> sendResult = send.get(timeOut, TimeUnit.MILLISECONDS);
            send.addCallback(successCallback -> {
                RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                log.warn("kafka Producer发送消息成功！ topic=" + recordMetadata.topic() + ", offset=" + recordMetadata.offset()
                        + ", partition=" + recordMetadata.partition());
            }, failureCallback -> {
                log.error("kafka Producer发送消息异常！ sendResult=" + sendResult.getProducerRecord());
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            log.error("同步消息发送超时");
        }
    }
}
