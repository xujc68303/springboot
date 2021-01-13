package com.xjc.guava.eventbus;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Version 1.0
 * @ClassName Bus
 * @Author jiachenXu
 * @Date 2021/1/13
 * @Description 消息总线
 */
public class Bus {

    @Test
    public void testSync(){
        EventBusCenter eventBusCenter = new EventBusCenter("test" , null, null);
        eventBusCenter.register(new ListenEvent());
        eventBusCenter.post("test message");
    }

    @Test
    public void testASync(){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        EventBusCenter eventBusCenter = new EventBusCenter("test" , "ASYNC", executorService);
        eventBusCenter.register(new ListenEvent());
        for(int i=0; i<=10; i++){
            eventBusCenter.post("test async message" + LocalDateTime.now());
        }
    }

}
