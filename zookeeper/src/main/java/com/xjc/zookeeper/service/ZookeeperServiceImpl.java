package com.xjc.zookeeper.service;

import com.xjc.zookeeper.api.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private CuratorFramework curatorFramework;

    private TreeCache treeCache;

    private String path = "/zk-1";

    private int sleepMsBetweenRetries;

    private int nThreads = 10;

    private volatile int count;

    private volatile InterProcessMutex interProcessMutex;

    private volatile DistributedAtomicInteger distributedAtomicInteger;

    private ExecutorService executorService = Executors.newFixedThreadPool(this.nThreads);

    @Override
    public Boolean exists(String path) throws Exception {
        Objects.requireNonNull(this.path);
        return curatorFramework.checkExists( ).forPath(path) != null;
    }

    @Override
    public Boolean asyncExists(String path) throws Exception {
        return curatorFramework.checkExists( ).inBackground((client, event) -> {
            log.warn("asyncExists, path={}, data={}", event.getPath( ), event.getData( ));
        }, executorService).forPath(path) != null;
    }

    @Override
    public String createNode(String path, String data) throws Exception {
        if (this.exists(path)) {
            return null;
        }
        return curatorFramework.create( ).withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes( ));
    }

    @Override
    public String getData(String path) throws Exception {
        return new String(curatorFramework.getData( ).forPath(path));
    }

    @Override
    public String getStat(String path) throws Exception {
        return new String(curatorFramework.getData( ).storingStatIn(new Stat( )).forPath(path));
    }

    @Override
    public Boolean updateNode(String path, String newData) throws Exception {
        return this.updateNode(path, newData, -1);
    }

    @Override
    public Boolean updateNode(String path, String data, int version) throws Exception {
        if (!this.exists(path)) {
            return false;
        }
        return curatorFramework.setData( ).withVersion(version).forPath(path, data.getBytes( )) != null;
    }

    @Override
    public Boolean deleteNode(String path, Boolean deleteChildren) throws Exception {
        return this.deleteNode(path, deleteChildren, -1);
    }

    @Override
    public Boolean deleteNode(String path, Boolean deleteChildren, int version) throws Exception {
        if (deleteChildren) {
            curatorFramework.delete( )
                    .guaranteed( )
                    .deletingChildrenIfNeeded( )
                    .withVersion(version)
                    .forPath(path);
        } else {
            curatorFramework.delete( )
                    .guaranteed( )
                    .withVersion(version)
                    .forPath(path);
        }
        return true;
    }

    @Override
    public Boolean asyncDeleteNode(String path, Boolean deleteChildren) throws Exception {
        return this.asyncDeleteNode(path, deleteChildren, -1);
    }

    @Override
    public Boolean asyncDeleteNode(String path, Boolean deleteChildren, int version) throws Exception {
        if (deleteChildren) {
            curatorFramework.delete( )
                    .guaranteed( )
                    .deletingChildrenIfNeeded( )
                    .withVersion(version)
                    .inBackground((client, event) -> {
                        log.warn("asyncDeleteNode, path={}, data={}", event.getPath( ), event.getData( ));
                    }, executorService).forPath(path);
        } else {
            curatorFramework.delete( )
                    .guaranteed( )
                    .withVersion(version)
                    .inBackground((client, event) -> {
                        log.warn("asyncDeleteNode, path={}, data={}", event.getPath( ), event.getData( ));
                    }, executorService).forPath(path);
        }
        return true;
    }

    @Override
    public List<String> getChildren(String path) throws Exception {
        if (this.exists(path)) {
            return null;
        }
        return curatorFramework.getChildren( ).forPath(path);
    }

    @Override
    public Boolean distributed(String path, int count) throws Exception {
        return this.distributed(path, 10,  -1, TimeUnit.MINUTES);
    }

    @Override
    public Boolean distributed(String path, int count, long time, TimeUnit unit) throws Exception {
        if (this.exists(path)) {
            return false;
        }
        this.count = count;
        this.interProcessMutex = new InterProcessMutex(this.curatorFramework, path);
        if(count != 0){
            while (true) {
                if (this.count >= 10) {
                    break;
                }
                if(this.interProcessMutex.acquire(time, unit)){
                    return true;
                } else {
                    count++;
                }
            }
        } else {
            return this.interProcessMutex.acquire(time, unit);
        }
        return false;
    }

    @Override
    public Boolean release(String path) throws Exception {
        if (this.exists(path)) {
            return false;
        }
        if (this.interProcessMutex != null) {
            this.interProcessMutex.release( );
            this.asyncDeleteNode(path, true);
        }
        return true;
    }

    @Override
    public AtomicValue<Integer> distributedCount(String path, int delta, int retryTime, int sleepMsBetweenRetries) throws Exception {
        if (!this.exists(path)) {
            return null;
        }
        this.sleepMsBetweenRetries = sleepMsBetweenRetries;
        RetryNTimes retryNTimes = new RetryNTimes(retryTime, this.sleepMsBetweenRetries);
        this.distributedAtomicInteger = new DistributedAtomicInteger(this.curatorFramework, path, retryNTimes);
        AtomicValue<Integer> result = this.distributedAtomicInteger.add(delta);
        if (result.succeeded( )) {
            return result;
        }
        return null;
    }

}
