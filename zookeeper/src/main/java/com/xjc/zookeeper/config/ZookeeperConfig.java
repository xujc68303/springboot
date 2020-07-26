package com.xjc.zookeeper.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/**
 * @Version 1.0
 * @ClassName ZookeeperConfig
 * @Author jiachenXu
 * @Date 2020/7/26 14:44
 * @Description
 */
@Slf4j
@Configuration
public class ZookeeperConfig {

    @Value("${zookeeper.url}")
    private String url;

    @Value("${zookeeper.timeout}")
    private int timeout;

    @Bean(name = "zkClient")
    public ZooKeeper zkClient() {
        ZooKeeper zooKeeper = null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            // 连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后直接调用后续
            // 可指定多台服务地址 127.0.0.1:2181, 127.0.0.1:2182, 127.0.0.1:2183
            zooKeeper = new ZooKeeper(url, timeout, new Watcher( ) {
                @Override
                public void process(WatchedEvent event) {
                    if (Event.KeeperState.SyncConnected == event.getState( )) {
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown( );
                    }
                }
            });
            countDownLatch.await( );
        } catch (Exception e) {
            log.error("初始化ZooKeeper连接异常....】={}", e);
        }
        return zooKeeper;
    }

    @Bean(name = "curatorFramework")
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = null;
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(100, 3);
            curatorFramework = CuratorFrameworkFactory.builder( )
                    .connectString(this.url)
                    .sessionTimeoutMs(this.timeout)
                    .retryPolicy(retryPolicy)
                    .namespace("base")
                    .build( );

        } catch (Exception e) {
            log.error("初始化curatorFramework连接异常....】={}", e);
        }
        return curatorFramework;
    }

}
