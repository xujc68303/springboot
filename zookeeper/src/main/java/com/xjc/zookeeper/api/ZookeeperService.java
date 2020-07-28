package com.xjc.zookeeper.api;

import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
     *
     * @param path path
     * @return 执行结果
     * @throws Exception
     */
    Boolean exists(String path) throws Exception;

    /**
     * 异步节点是否存在
     *
     * @param path path
     * @return 执行结果
     * @throws Exception
     */
    Boolean asyncExists(String path) throws Exception;

    /**
     * 创建节点
     *
     * @param path path
     * @return data
     * @throws Exception
     */
    String createNode(String path) throws Exception;

    /**
     * 创建节点
     *
     * @param path path
     * @param data data
     * @return data
     * @throws Exception
     */
    String createNode(String path, String data) throws Exception;

    /**
     * 获取节点data
     *
     * @param path path
     * @return
     * @throws Exception
     */
    String getData(String path) throws Exception;

    /**
     * 同步数据并获取节点data
     * @param path
     * @return
     */
    String synGetData(String path) throws Exception;

    /**
     * 获取节点信息
     *
     * @param path path
     * @return
     * @throws Exception
     */
    String getStat(String path) throws Exception;

    /**
     * 修改节点
     *
     * @param path    path
     * @param newData 修改数据
     * @return 执行结果
     * @throws Exception
     */
    Boolean updateNode(String path, String newData) throws Exception;

    /**
     * 修改节点
     *
     * @param path    path
     * @param newData 修改数据
     * @param version 版本
     * @return 执行结果
     * @throws Exception
     */
    Boolean updateNode(String path, String newData, int version) throws Exception;

    /**
     * 删除节点
     *
     * @param path           path
     * @param deleteChildren 是否删除子节点
     * @return 执行结果
     * @throws Exception
     */
    Boolean deleteNode(String path, Boolean deleteChildren) throws Exception;

    /**
     * 删除节点
     *
     * @param path           path
     * @param deleteChildren 是否删除子节点
     * @param version        版本
     * @return 执行结果
     * @throws Exception
     */
    Boolean deleteNode(String path, Boolean deleteChildren, int version) throws Exception;

    /**
     * 异步删除节点
     *
     * @param path           path
     * @param deleteChildren 是否删除子节点
     * @return 执行结果
     * @throws Exception
     */
    Boolean asyncDeleteNode(String path, Boolean deleteChildren) throws Exception;

    /**
     * 异步删除节点
     *
     * @param path           path
     * @param deleteChildren 是否删除子节点
     * @param version        版本
     * @return 执行结果
     * @throws Exception
     */
    Boolean asyncDeleteNode(String path, Boolean deleteChildren, int version) throws Exception;

    /**
     * 获取节点下的子节点
     *
     * @param path path
     * @return 子节点
     * @throws Exception
     */
    List<String> getChildren(String path) throws Exception;

    /**
     * 分布式锁
     *
     * @param path path
     * @return 执行结果
     * @throws Exception
     */
    Boolean distributed(String path, int count) throws Exception;

    /**
     * 分布式锁
     *
     * @param path  path
     * @param count 自旋次数
     * @param time  加锁时间
     * @param unit  时间单位
     * @return 执行结果
     * @throws Exception
     */
    Boolean distributed(String path, int count, long time, TimeUnit unit) throws Exception;

    /**
     * 解锁
     *
     * @param path path
     * @return 执行结果
     * @throws Exception
     */
    Boolean release(String path) throws Exception;

    /**
     * 分布式计数器
     *
     * @param path                  path
     * @param delta                 每次增加数量
     * @param retryTime             最大重试
     * @param sleepMsBetweenRetries 重试的时间 s
     * @return
     * @throws Exception
     */
    AtomicValue<Integer> distributedCount(String path, int delta, int retryTime, int sleepMsBetweenRetries) throws Exception;

    /**
     * 获取读写锁
     *
     * @param path path
     * @return
     * @throws Exception
     */
    InterProcessReadWriteLock getReadWriteLock(String path) throws Exception;

    /**
     * 服务注册
     *
     * @param path path
     * @param data data
     * @return
     */
    String serviceRegistry(String path, String data) throws Exception;

}
