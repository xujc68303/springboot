package com.xjc.producer.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author jiachenxu
 * @Date 2021/12/15
 * @Descripetion
 */

public class MailProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {


    }

}
