package com.xjc.guava.eventbus;

import org.junit.Test;

/**
 * @Version 1.0
 * @ClassName Bus
 * @Author jiachenXu
 * @Date 2021/1/13
 * @Description 消息总线
 */
public class Bus {

    @Test
    public void testEventBus(){
        EventBusCenter eventBusCenter = new EventBusCenter("test");
        eventBusCenter.register(new ListenEvent());
        eventBusCenter.post("test message");
    }

}
