package com.util.loadcache.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName LoadingCacheService
 * @Author jiachenXu
 * @Date 2020/9/1
 * @Description 本地缓存
 */
public interface LoadingCacheService {

    /**
     * 修改缓存配置
     *
     * @param maxSize         缓存容量最大值
     * @param duration        缓存过期时间
     * @param timeUnit        时间单位
     * @param initialCapacity 初始化缓存容量
     * @param refresh         缓存刷新时间
     */
    void modifyConfig(long maxSize, long duration, TimeUnit timeUnit, int initialCapacity, long refresh);

    /**
     * 添加数据
     *
     * @param key   key
     * @param value value
     * @return 执行结果
     */
    boolean put(String key, Object value);

    /**
     * 批量添加数据
     *
     * @param map map
     * @return 执行结果
     */
    boolean putAll(Map<String, Object> map);

    /**
     * 获取value
     *
     * @param key key
     * @return value
     */
    Object get(String key);

    /**
     * 批量获取value
     *
     * @param keys keys
     * @return values
     */
    Map<String, Object> getAll(List<String> keys);

    /**
     * 获取缓存全部数据
     *
     * @return
     */
    Map<String, Object> asMap();

    /**
     * 根据key删除
     *
     * @param key key
     * @return 执行结果
     */
    boolean delete(String key);

    /**
     * 根据key批量删除
     *
     * @param keys keys
     * @return 执行结果
     */
    boolean deleteAll(List<String> keys);

    /**
     * 缓存长度
     *
     * @return
     */
    long size();

    /**
     * 清空缓存
     *
     * @return
     */
    boolean clear();

}
