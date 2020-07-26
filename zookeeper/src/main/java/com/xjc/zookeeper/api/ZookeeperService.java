package com.xjc.zookeeper.api;

import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.poi.ss.formula.functions.T;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Version 1.0
 * @ClassName ZookeeperService
 * @Author jiachenXu
 * @Date 2020/7/26 15:01
 * @Description
 */
public interface ZookeeperService {

    /**
     * 节点是否存在
     * @param path path
     * @param needWatch 是否复用监听类
     * @return 执行结果
     * @throws KeeperException
     * @throws InterruptedException
     */
    Boolean exists(String path, boolean needWatch) throws KeeperException, InterruptedException;

    /**
     * 节点是否存在
     * @param path path
     * @param watcher 监听类
     * @return 执行结果
     * @throws KeeperException
     * @throws InterruptedException
     */
    Boolean exists(String path, Watcher watcher) throws KeeperException, InterruptedException;

    /**
     * 创建节点
     * @param path path
     * @param data data
     * @return data
     * @throws KeeperException
     * @throws InterruptedException
     */
    String createNode(String path, String data) throws KeeperException, InterruptedException;

    /**
     * 获取节点data
     * @param path path
     * @param watcher 监听类
     * @return data
     * @throws KeeperException
     * @throws InterruptedException
     */
    String getData(String path, Watcher watcher) throws KeeperException, InterruptedException;

    /**
     * 获取节点信息
     * @param path path
     * @return
     */
    Stat getStat(String path) throws KeeperException, InterruptedException;

    /**
     * 修改节点
     * @param path path
     * @param newData 修改数据
     * @return 执行结果
     * @throws KeeperException
     * @throws InterruptedException
     */
    Boolean updateNode(String path, String newData) throws KeeperException, InterruptedException;

    /**
     * 删除节点
     * @param path path
     * @param deleteChildren 是否删除子节点
     * @return 执行结果
     * @throws KeeperException
     * @throws InterruptedException
     */
    Boolean deleteNode(String path, Boolean deleteChildren) throws Exception;

    /**
     * 获取节点下的子节点
     * @param path path
     * @param needWatch 是否复用监听类
     * @return 子节点
     * @throws KeeperException
     * @throws InterruptedException
     */
    List<String> getChildren(String path, boolean needWatch) throws KeeperException, InterruptedException;

    /**
     * 分布式锁
     * @param path path
     * @return 执行结果
     * @throws Exception
     */
    Boolean distributed(String path) throws Exception;

    /**
     * 分布式锁
     * @param path path
     * @param time 加锁时间
     * @param unit 时间单位
     * @return 执行结果
     * @throws Exception
     */
    Boolean distributed(String path, long time, TimeUnit unit) throws Exception;

    /**
     * 解锁
     * @param path path
     * @return 执行结果
     * @throws Exception
     */
    Boolean tryLock(String path) throws Exception;

    /**
     * 分布式计数器
     * @param path path
     * @param delta 每次增加数量
     * @param retryTime 最大重试
     * @param sleepMsBetweenRetries 重试的时间 s
     * @return
     * @throws Exception
     */
    AtomicValue<Integer> distributedCount(String path, int delta, int retryTime, int sleepMsBetweenRetries) throws Exception;

}
