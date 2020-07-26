package com.xjc.zookeeper.service;

import com.xjc.zookeeper.api.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    private TreeCache treeCache;

    private volatile String path;

    private volatile int retryTime;

    private volatile int sleepMsBetweenRetries;

    private volatile InterProcessMutex interProcessMutex = new InterProcessMutex(this.curatorFramework, this.path);

    private volatile DistributedAtomicInteger distributedAtomicInteger =
            new DistributedAtomicInteger(this.curatorFramework, this.path, new RetryNTimes(this.retryTime, this.sleepMsBetweenRetries));

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
    public Boolean deleteNode(String path, Boolean deleteChildren) throws Exception {
        if(deleteChildren){
            List<String> childrens = this.getChildren(path, false);
            childrens.stream().filter(Objects::nonNull).forEach(node -> {
                try {
                    zkClient.delete(node, -1);
                } catch (Exception e) {
                    log.error("deleteNode error", e);
                }
            });
        } else {
            zkClient.delete(path, -1);
        }

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

    @Override
    public AtomicValue<Integer> distributedCount(String path, int delta, int retryTime, int sleepMsBetweenRetries) throws Exception {
        if (!this.exists(path, true)) {
            return null;
        }
        Objects.requireNonNull(this.path);
        this.path = path;
        this.retryTime = retryTime;
        this.sleepMsBetweenRetries = sleepMsBetweenRetries;
        AtomicValue<Integer> result = this.distributedAtomicInteger.add(delta);
        if(result.succeeded()){
            return result;
        }
        return null;
    }

}
