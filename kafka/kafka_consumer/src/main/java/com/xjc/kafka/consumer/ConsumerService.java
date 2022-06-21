package com.xjc.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ConsumerService {

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "xjc", partitions = {"0", "1", "3", "4"})})
    public void listen(ConsumerRecord<String, Object> consumerRecord, Acknowledgment acknowledgment) {
        Optional<Object> value = Optional.ofNullable(consumerRecord.value());
        if (value.isPresent()) {
            Object message = value.get();
            log.info("topic="+ consumerRecord.topic()+" partition="+consumerRecord.partition()+"消费者开始消费message" + message);
        }
    }

}
