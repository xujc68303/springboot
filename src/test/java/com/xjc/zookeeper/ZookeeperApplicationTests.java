package com.xjc.zookeeper;

import com.alibaba.fastjson.JSON;
import com.xjc.zookeeper.api.WatcherService;
import com.xjc.zookeeper.api.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class ZookeeperApplicationTests {

    @Autowired
    private ZookeeperService zookeeperService;

    @Test
    public void createNodeTest() throws KeeperException, InterruptedException {
        String result = zookeeperService.createNode("/zk-watcher-1", "test");
        log.warn("result:" + result);
    }

    @Test
    public void getDataTest() throws KeeperException, InterruptedException {
        String result = zookeeperService.getData("/zk-watcher-1", new WatcherService());
        log.warn("result:" + result);
    }

    @Test
    public void updateNodeTest() throws KeeperException, InterruptedException {
        Boolean result = zookeeperService.updateNode("/zk-watcher-1", "update");
        log.warn("result:" + result);
    }

    @Test
    public void deleteNodeTest() throws KeeperException, InterruptedException {
        Boolean result = zookeeperService.deleteNode("/zk-watcher-1");
        log.warn("result:" + result);
    }

    @Test
    public void getChildrenTest() throws KeeperException, InterruptedException {
        List<String> result = zookeeperService.getChildren("/zk-watcher-1", true);
        log.warn("result:" + JSON.toJSONString( result));
    }

}
