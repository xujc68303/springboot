package com.xjc.guava.eventbus;

import com.google.common.eventbus.Subscribe;

/**
 * @Version 1.0
 * @ClassName ListenEvent
 * @Author jiachenXu
 * @Date 2021/1/13
 * @Description
 */
public class ListenEvent {

    @Subscribe
    public void consumer(String msg) {
        System.out.println("String msg: " + msg);
    }

}
