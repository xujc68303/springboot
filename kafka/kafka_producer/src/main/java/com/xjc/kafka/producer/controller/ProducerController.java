package com.xjc.kafka.producer.controller;

import com.xjc.kafka.producer.service.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author jiachenxu
 * @Date 2021/4/1
 * @Descripetion
 */
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private ProducerService producerService;

    @RequestMapping("/send")
    public void send(@RequestBody Map<Object, Object> map){
        producerService.syncSend("xjc", map, null);
    }


}
