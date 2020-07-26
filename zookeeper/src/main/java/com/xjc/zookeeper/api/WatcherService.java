package com.xjc.zookeeper.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @Version 1.0
 * @ClassName Watcher
 * @Author jiachenXu
 * @Date 2020/7/26 15:24
 * @Description
 */
@Slf4j
public class WatcherService implements Watcher {

    @Override
    public void process(WatchedEvent event) {
        //  三种监听类型： 创建，删除，更新
        log.info("【Watcher监听事件】={}", event.getState( ));
        log.info("【监听路径为】={}", event.getPath( ));
        log.info("【监听的类型为】={}", event.getType( ));
    }
}
