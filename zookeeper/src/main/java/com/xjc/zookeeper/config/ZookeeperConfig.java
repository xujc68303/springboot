package com.xjc.zookeeper.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Value("${zookeeper.retries}")
    private int retries;

    @Value("${zookeeper.baseSleepTimeMs}")
    private int baseSleepTimeMs;

    @Bean(name = "curatorFramework")
    public CuratorFramework curatorFramework() {
        CuratorFramework curatorFramework = null;
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, retries);
            curatorFramework = CuratorFrameworkFactory.builder( )
                    .connectString(this.url)
                    .sessionTimeoutMs(this.timeout)
                    .retryPolicy(retryPolicy)
                    .build( );
            curatorFramework.start( );
        } catch (Exception e) {
            log.error("初始化curatorFramework连接异常....】={}", e);
        }
        return curatorFramework;
    }

    public void addListener(CuratorFramework curatorFramework, String path) throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, path, false);
        nodeCache.getListenable( ).addListener(() -> {
            log.warn("path : " + nodeCache.getCurrentData( ).getPath( ));
            log.warn("data : " + new String(nodeCache.getCurrentData( ).getData( )));
            log.warn("stat : " + nodeCache.getCurrentData( ).getStat( ));
        });
        nodeCache.start( );
    }

    public void addListener(CuratorFramework curatorFramework) throws Exception {
        //设置节点的cache
        TreeCache treeCache = new TreeCache(curatorFramework, "/test");
        //设置监听器和处理过程
        treeCache.getListenable( ).addListener((client, event) -> {
                    ChildData data = event.getData( );
                    if (data != null) {
                        switch (event.getType( )) {
                            case NODE_ADDED:
                                System.out.println("NODE_ADDED : " + data.getPath( ) + "  数据:" + new String(data.getData( )));
                                break;
                            case NODE_REMOVED:
                                System.out.println("NODE_REMOVED : " + data.getPath( ) + "  数据:" + new String(data.getData( )));
                                break;
                            case NODE_UPDATED:
                                System.out.println("NODE_UPDATED : " + data.getPath( ) + "  数据:" + new String(data.getData( )));
                                break;
                            default:
                                break;
                        }
                    } else {
                        System.out.println("data is null : " + event.getType( ));
                    }

                }

        );
        treeCache.start( );
    }

}
