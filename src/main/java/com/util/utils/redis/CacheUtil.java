package com.util.utils.redis;

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
     * @param key
     * @return
     */
    Boolean exists(String key);

    /**
     * 根据Key获取value（Object）
     * @param key cacheKey
     * @return value
     */
    Object get(String key);

    /**
     * 根据Key获取value（String）
     * @param key cacheKey
     * @return value
     */
    String getString(String key);

    /**
     * 添加key & Data 并设置过期时间
     * @param key cacheKey
     * @param value data
     * @param expire 过期时间 秒
     * @return 执行结果
     */
    Boolean setWithExpire(String key, Object value, int expire);

    /**
     * 给Key重新设置过期时间并获取Value
     * @param key cacheKey
     * @param expire 过期时间 秒
     * @return
     */
    Object getKeyWithExpire(String key, int expire);

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
     * @return 执行结果
     */
    Boolean renameByKey(String oldKey, String newKey);

    /**
     * 分布式锁（手动解锁）
     * @param key cacheKey
     * @param value data
     * @return 执行结果
     */
    boolean distributedLock(String key, Object value);

    /**
     * 分布式锁
     * @param key cacheKey
     * @param value data
     * @param expx 过期时间单位,可空 默认秒
     * @param expire 过期时间
     * @return 执行结果
     */
    boolean distributedLock(String key, Object value, String expx, Long expire);

    /**
     * 秒杀锁
     * @param key cacheKey
     * @param value data
     * @param expx 过期时间单位,可空 默认秒
     * @param expire 过期时间
     * @param lockWaitTimeOut 总持续时间
     * @return 执行结果
     */
    boolean preemptiveLock(String key, Object value, String expx, String expire, Long lockWaitTimeOut);

    /**
     * 解除锁
     * @param key cacheKey
     * @param value data
     * @return 执行结果
     */
    Boolean unlock(String key, Object value);

}
