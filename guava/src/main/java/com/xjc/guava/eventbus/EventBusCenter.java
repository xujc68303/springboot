package com.xjc.guava.eventbus;

import com.google.common.eventbus.EventBus;

/**
 * @Version 1.0
 * @ClassName EventBusCenter
 * @Author jiachenXu
 * @Date 2021/1/13
 * @Description
 */
public class EventBusCenter {

    private EventBus eventBus;

    public EventBusCenter(String name) {
        eventBus = new EventBus(name);
    }

    public EventBus getEventBus(){
       return this.eventBus;
    }

    public void register(Object obj) {
        eventBus.register(obj);
    }

    public void unregister(Object obj) {
        eventBus.unregister(obj);
    }

    public void post(Object obj) {
        eventBus.post(obj);
    }

}
