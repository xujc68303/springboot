package com.xjc.redis.api;

import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName RedissonService
 * @Author jiachenXu
 * @Date 2020/9/1 23:06
 * @Description
 */
public interface RedissonService {

    /**
     * 限流器
     *
     * @param serviceName 名称
     * @param maxFlow     时段最大流量
     * @param section     时间区段
     * @param expired     过期时间
     * @param unit        时间单位
     * @return
     */
    RRateLimiter currentLimiting(String serviceName, long maxFlow, long section, long expired, TimeUnit unit);

    /**
     * 获得限流器配置
     *
     * @param serviceName 名称
     * @return
     */
    RateLimiterConfig getLimiterConfig(String serviceName);

    /**
     * 清空限流器过期时间
     *
     * @param serviceName 名称
     * @return
     */
    boolean clearLimiting(String serviceName);

    /**
     * 布隆过滤器初始化
     *
     * @param maxSize 最大容量
     * @param fpp     误报率
     * @return
     */
    boolean initBloomFilter(long maxSize, double fpp);

    /**
     * 布隆过滤器添加过滤词
     *
     * @param pattern 过滤词
     * @return
     */
    boolean addBloomFilter(String pattern);

    /**
     * 布隆过滤器添加过滤词
     *
     * @param patterns 过滤词
     * @return
     */
    boolean addBloomFilter(List<String> patterns);

    /**
     * 布隆过滤器过滤
     *
     * @param pattern 过滤词
     * @return
     */
    boolean matchBloomFilter(String pattern);

    /**
     * 移除当前布隆过滤器
     *
     * @return
     */
    boolean clearBloomFilter();

    /**
     * 可重入锁
     *
     * @param lockName 锁名称
     * @param wait     获取锁等待时间
     * @param expired  锁过期时间
     * @param timeUnit 时间单位
     * @return
     */
    RLock reentrantLock(String lockName, long wait, long expired, TimeUnit timeUnit);

    /**
     * 公平锁
     *
     * @param lockName 锁名称
     * @param wait     获取锁等待时间
     * @param expired  锁过期时间
     * @param timeUnit 时间单位
     * @return
     */
    RLock fairLock(String lockName, long wait, long expired, TimeUnit timeUnit);

    /**
     * 多重关联锁
     *
     * @param wait     获取锁等待时间
     * @param expired  锁过期时间
     * @param timeUnit 时间单位
     * @param lockName 锁名称
     * @return
     */
    RedissonMultiLock multiLock(long wait, long expired, TimeUnit timeUnit, String... lockName);

    /**
     * 多重关联锁（成功一半也算成功）
     *
     * @param wait     获取锁等待时间
     * @param expired  锁过期时间
     * @param timeUnit 时间单位
     * @param lockName 锁名称
     * @return
     */
    RedissonRedLock readLock(long wait, long expired, TimeUnit timeUnit, String... lockName);

    /**
     * 线程同步 信号量控制器
     *
     * @param serviceName 名称
     * @param permits     信号总量
     * @param wait        等待时间
     * @param expired     过期时间
     * @param timeUnit    时间单位
     * @return
     */
    RSemaphore semaphore(String serviceName, int permits, long wait, long expired, TimeUnit timeUnit);

    /**
     * 删除信号量控制器
     *
     * @param serviceName
     * @return
     */
    boolean deleteSemaphore(String serviceName);

    /**
     * 线程同步 计数器
     *
     * @param serviceName 名称
     * @param count       计数总量
     * @return
     */
    RCountDownLatch countDownLatch(String serviceName, int count);

    /**
     * 对象桶添加
     * @param map map
     * @return
     */
    boolean setBucket(Map<String, Object> map);

    /**
     * 对象桶 根据key获取对象
     * @param keys keys
     * @return
     */
    Map<String, Object> getBuckets(List<String> keys);

    /**
     * 对象桶 根据key删除对象
     * @param keys keys
     * @return 成功删除数量
     */
    long deleteBuckets(List<String> keys);

}
