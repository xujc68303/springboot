package com.xjc.aop.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/log")
public class LogTest {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/test")
    public void test() {
        Map<String, Object> oldValues = new HashMap<>();
        oldValues.put("name", "xjc");
        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", "xj1");
        operationLogService.logFieldChanges("delivery_point", "1", "模块1", oldValues, newValues);


    }


}
