package com.xjc.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @Author jiachenxu
 * @Date 2021/12/15
 * @Descripetion
 */
@Slf4j
@Configuration
public class RabbitProducerConfiguration {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    /**
     * 消息的确认指的是生产者投递消息后, 如果 Broker 收到消息, 给生产者应答.<br>
     * 生产者接收应答用来确认消息是否正常的发送到 Broker, 这种方式也是 Producer 端消息的 <strong>可靠性投递</strong> 的核心保障.
     */
    private final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        final String messageId = Optional.ofNullable(correlationData).orElseThrow(() -> new RuntimeException("correlationData is null!")).getId();

        if (ack) {
            // ~ [ ! ] 如果消息发送到 Exchange 成功, 但是 routingKey 错误, 则会触发 ReturnCallback,
            //         ※ 只要 Exchange 正确, 消息成功发送到 Broker, confirmCallback 的 ack = {@code true}
            log.info("Producer :: 消息发送到 Broker - 成功.");

            // ~ 插入成功发送的日志记录
//            messageDeliveryLogService.deliverySuccess(messageId);
        } else {
            // ~ [ ! ] 如果生产端发送消息时, 指定了错误的 Exchange, 则消息并没有发送到 Broker, ack = {@code false}

            log.error("Producer :: 消息发送到 Broker - 失败. correlationData: {} cause: {}", correlationData, cause);

            // ~ 消息都没发送到 Broker
//            messageDeliveryLogService.deliveryFailed(messageId);
        }
    };

    /**
     * 用于处理一些 <strong>不可路由</strong> 的消息.
     */
    private final RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> {
        // ~ [ ! ] 如果发送到 Exchange 成功, 但是 RoutingKey 不正确:
        //         ConfirmCallback ack = {@code true}, 但是 ReturnCallback 也会调用; 需要注意 ReturnCallback 和 ConfirmCallback 的调用顺序.

        log.error("Producer :: 消息从 Exchange 路由到 Queue 失败: exchange: {}, routingKey: {}, replyCode: {}, replayText: {}, message: {}",
                exchange, routingKey, replyCode, replyText, message);
        final String messageId = MapUtils.getString(message.getMessageProperties().getHeaders(), PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY);

        //  消息发送到了 Broker 但是不可路由, Consumer 端也应该监控这种投递成功但是隔了一段时间还没有消费的消息
    };


    @Bean
    public RabbitTemplate rabbitTemplate() {
        // 开启confirm模式
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback(confirmCallback);
        //    在消息没有被路由到合适的队列情况下，Broker 会将消息返回给生产者，
        //    为 true 时如果 Exchange 根据类型和消息 Routing Key 无法路由到一个合适的 Queue 存储消息，
        //        Broker 会调用 Basic.Return 回调给 handleReturn()，再回调给 ReturnCallback，将消息返回给生产者。
        //    为 false 时，丢弃该消息
        rabbitTemplate.setMandatory(Boolean.TRUE);
        rabbitTemplate.setReturnCallback(returnCallback);
        return rabbitTemplate;
    }

    @Bean
    public MessagePostProcessor messagePostProcessor() {
        return message -> {
            final MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding(StandardCharsets.UTF_8.name());
            return message;
        };
    }

}
