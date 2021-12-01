package com.xjc.guava.eventbus;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executor;

/**
 * @Version 1.0
 * @ClassName EventBusCenter
 * @Author jiachenXu
 * @Date 2021/1/13
 * @Description
 */
public class EventBusCenter {

    private EventBus eventBus;

    public EventBusCenter(String name, String type, Executor executor) {
        if (type == "ASYNC") {
            if (eventBus == null) {
                synchronized (this) {
                    if (eventBus == null) {
                        eventBus = new EventBus(name);
                    }
                }
            }
        } else {
            if (eventBus == null) {
                synchronized (this) {
                    if (eventBus == null) {
                        eventBus = new AsyncEvent(name, executor);
                    }
                }
            }
        }
    }

    public EventBus getEventBus() {
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

    class SysncEvent extends EventBus {

        public SysncEvent(String name) {

        }
    }

    class AsyncEvent extends EventBus {

        public AsyncEvent(String identifier, Executor executor) {

        }
    }

}
