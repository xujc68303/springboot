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

    private volatile CuratorFramework curatorFramework;

    private volatile NodeCache nodeCache;

    private volatile TreeCache treeCache;

    @Bean(name = "curatorFramework")
    public CuratorFramework curatorFramework() {
        try {
            synchronized (ZookeeperConfig.class) {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, retries);
                curatorFramework = CuratorFrameworkFactory.builder( )
                        .connectString(this.url)
                        .sessionTimeoutMs(this.timeout)
                        .retryPolicy(retryPolicy)
                        .build( );
                curatorFramework.start( );
            }
        } catch (Exception e) {
            log.error("初始化curatorFramework连接异常....】={}", e);
        }
        return curatorFramework;
    }

    public void addListener(CuratorFramework curatorFramework, String path) throws Exception {
        synchronized (NodeCache.class) {
            if (nodeCache == null) {
                nodeCache = new NodeCache(curatorFramework, path, false);
            }
            nodeCache.getListenable( ).addListener(() -> {
                log.warn("path : " + nodeCache.getCurrentData( ).getPath( ));
                log.warn("data : " + new String(nodeCache.getCurrentData( ).getData( )));
                log.warn("stat : " + nodeCache.getCurrentData( ).getStat( ));
            });
            nodeCache.start( );
        }
    }

    public TreeCache addTreeCache(CuratorFramework curatorFramework, String path) throws Exception {
        synchronized (TreeCache.class) {
            if (treeCache == null) {
                //设置节点的cache
                treeCache = new TreeCache(curatorFramework, path);
            }

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
        return treeCache;
    }

}
