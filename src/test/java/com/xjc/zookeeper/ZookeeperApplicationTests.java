package com.xjc.zookeeper;

import com.alibaba.fastjson.JSON;
import com.xjc.zookeeper.api.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
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
    public void createNodeTest() throws Exception {
        String result = zookeeperService.createNode("/zk-watcher-1", "test");
        log.warn("result:" + result);
    }

    @Test
    public void getDataTest() throws Exception {
        String result = zookeeperService.getData("/zk-watcher-1");
        log.warn("result:" + result);
    }

    @Test
    public void updateNodeTest() throws Exception {
        Boolean result = zookeeperService.updateNode("/zk-watcher-1", "update");
        log.warn("result:" + result);
    }

    @Test
    public void deleteNodeTest() throws Exception {
        Boolean result = zookeeperService.deleteNode("/zk-watcher-1", false);
        log.warn("result:" + result);
    }

    @Test
    public void getChildrenTest() throws Exception {
        List<String> result = zookeeperService.getChildren("/zk-watcher-1");
        log.warn("result:" + JSON.toJSONString( result));
    }

}
