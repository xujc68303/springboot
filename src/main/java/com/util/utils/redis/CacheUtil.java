package com.util.utils.redis;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName CacheUtil
 * @Author jiachenXu
 * @Date 2020/3/5 13:38
 * @Description 缓存工具类
 */
public interface CacheUtil {

    /**
     * 检查key是否存在
     * @param key cacheKey
     * @return 执行结果
     */
    Boolean exists(String key);

    /**
     * 根据Key获取value（Object）
     * @param key cacheKey
     * @return value
     */
    Object get(String key);

    /**
     * 添加key & Data 并设置过期时间
     * @param key cacheKey
     * @param value data
     * @param expire 过期时间
     * @param unit 时间类型
     * @return 执行结果
     */
    Boolean setWithExpire(String key, Object value, long expire, TimeUnit unit);

    /**
     * 给Key重新设置过期时间并获取Value
     * @param key cacheKey
     * @param expire 过期时间
     * @param unit 时间类型
     * @return Value
     */
    Object getKeyWithExpire(String key, long expire, TimeUnit unit);

    /**
     * 删除data
     * @param key cacheKey
     * @return 执行结果
     */
    Boolean removeObject(String key);

    /**
     * 修改key名称
     * @param oldKey 原有key
     * @param newKey 修改key
     */
    void renameByKey(String oldKey, String newKey);

    /**
     * 分布式锁
     * @param key cacheKey
     * @param value data
     * @param expx 过期时间单位, ex px
     * @param expire 过期时间
     * @param unit 时间类型
     * @return 执行结果
     */
    boolean distributedLock(String key, Object value, @NotNull String expx, @NotNull long expire, @NotNull TimeUnit unit);

    /**
     * 秒杀锁
     * @param key cacheKey
     * @param value data
     * @param lockWaitTimeOut 总持续时间
     * @param unit 时间类型
     * @return 执行结果
     */
    boolean preemptiveLock(String key, Object value, long lockWaitTimeOut, TimeUnit unit);

    /**
     * 解除锁
     * @param key cacheKey
     * @param value data
     * @return 执行结果
     */
    Boolean unlock(String key, Object value);

}
