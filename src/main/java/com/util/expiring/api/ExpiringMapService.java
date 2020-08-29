package com.util.expiring.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName ExpiringMapService
 * @Author jiachenXu
 * @Date 2020/8/29 21:53
 * @Description 本地缓存ExpiringMap
 */
public interface ExpiringMapService {

    /**
     * 修改缓存配置
     *
     * @param maxSize    最大容量
     * @param timeUnit   缓存过期时间单位
     * @param expiration 缓存过期时间
     */
    void modifyConfig(int maxSize, TimeUnit timeUnit, long expiration);

    /**
     * k 是否存在
     *
     * @param k k
     * @return 执行结果
     */
    boolean isExist(String k);

    /**
     * 添加数据
     *
     * @param k          k
     * @param v          v
     * @param timeUnit   缓存过期时间单位
     * @param expiration 缓存过期时间
     * @return 执行结果
     */
    boolean put(String k, Object v, TimeUnit timeUnit, long expiration);

    /**
     * 添加数据, k存在不添加
     *
     * @param k k
     * @param v v
     */
    boolean putIfAbsent(String k, Object v);

    /**
     * 批量添加数
     *
     * @param map map
     * @return 执行结果
     */
    boolean putAll(Map<String, Object> map);

    /**
     * 删除数据
     *
     * @param k k
     * @return 执行结果
     */
    boolean del(String k);

    /**
     * 获取数据
     *
     * @param k k
     * @return 映射v
     */
    Object get(String k);

    /**
     * 更新数据
     *
     * @param k k
     * @param v v
     * @return 执行结果
     */
    boolean update(String k, Object v);

    /**
     * 获取缓存长度
     * @return
     */
    long getSize();

    /**
     * 获取全部k
     * @return
     */
    List<String> getKeys();

    /**
     * 获取全部缓存
     * @return
     */
    Map<String, Object> getAll();

    /**
     * 清空数据
     */
    void clear();

}
