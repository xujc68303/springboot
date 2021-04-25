package com.xjc.kafka.producer.config;

import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Properties;

@EnableKafka
@Configuration
public class KakfaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String server;

    public static final String TOPIC = "xjc";

    @Bean
    public NewTopic newTopic() {
        // 创建topic， 需要指定创建的topic名称、分区数、副本数量（小于broker量）
        return new NewTopic(TOPIC, 8, (short) 1);
    }

    @Bean
    public KafkaAdminClient kafkaAdminClient(){
        Properties props = new Properties();
        props.put("bootstrap.servers", server);
        return (KafkaAdminClient) KafkaAdminClient.create(props);
    }

}
