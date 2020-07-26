package com.xjc.zookeeper.service;

import com.xjc.zookeeper.api.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName ZookeeperServiceImpl
 * @Author jiachenXu
 * @Date 2020/7/26 15:01
 * @Description
 */
@Slf4j
@Service
public class ZookeeperServiceImpl implements ZookeeperService {

    @Autowired
    private ZooKeeper zkClient;

    @Autowired
    private CuratorFramework curatorFramework;

    private volatile String path;

    private volatile InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework, path);

    @Override
    public Boolean exists(String path, boolean needWatch) throws KeeperException, InterruptedException {
        return zkClient.exists(path, needWatch) != null;
    }

    @Override
    public Boolean exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zkClient.exists(path, watcher) != null;
    }

    @Override
    public String createNode(String path, String data) throws KeeperException, InterruptedException {
        if (this.exists(path, true)) {
            return null;
        }
        return zkClient.create(path, data.getBytes( ), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Override
    public String getData(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return new String(zkClient.getData(path, watcher, new Stat( )));
    }

    @Override
    public Stat getStat(String path) throws KeeperException, InterruptedException {
        return zkClient.exists(path, true);
    }

    @Override
    public Boolean updateNode(String path, String data) throws KeeperException, InterruptedException {
        if (!this.exists(path, true)) {
            return false;
        }
        return zkClient.setData(path, data.getBytes( ), -1) != null;
    }

    @Override
    public Boolean deleteNode(String path) throws KeeperException, InterruptedException {
        zkClient.delete(path, -1);
        return true;
    }

    @Override
    public List<String> getChildren(String path, boolean needWatch) throws KeeperException, InterruptedException {
        if (this.exists(path, true)) {
            return null;
        }
        return zkClient.getChildren(path, needWatch);
    }

    @Override
    public Boolean distributed(String path) throws Exception {
        this.distributed(path, -1, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Boolean distributed(String path, long time, TimeUnit unit) throws Exception {
        if (this.exists(path, true)) {
            return false;
        }
        this.path = path;
        return this.interProcessMutex.acquire(time, unit);
    }

    @Override
    public Boolean tryLock(String path) throws Exception {
        if (this.exists(path, true)) {
            return false;
        }
        this.interProcessMutex.release( );
        return true;
    }

}
