package com.util.redis;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName CacheService
 * @Author jiachenXu
 * @Date 2020/3/5 13:38
 * @Description 缓存工具类
 */
public interface CacheService {

    /**
     * 检查key是否存在
     *
     * @param key cacheKey
     * @return 执行结果
     */
    Boolean exists(String key);

    // string

    /**
     * 根据Key获取value（Object）
     *
     * @param key cacheKey
     * @return value
     */
    Object get(String key);

    /**
     * 添加key & Data 并设置过期时间
     *
     * @param key    cacheKey
     * @param value  data
     * @param expire 过期时间
     * @param unit   时间类型
     * @return 执行结果
     */
    Boolean setWithExpire(String key, Object value, long expire, TimeUnit unit);

    /**
     * 给Key重新设置过期时间并获取Value
     *
     * @param key    cacheKey
     * @param expire 过期时间
     * @param unit   时间类型
     * @return Value
     */
    Object getKeyWithExpire(String key, long expire, TimeUnit unit);

    /**
     * 删除data
     *
     * @param key cacheKey
     * @return 执行结果
     */
    Boolean delete(String key);

    /**
     * 修改key名称
     *
     * @param oldKey 原有key
     * @param newKey 修改key
     */
    Boolean renameByKey(String oldKey, String newKey);

    /**
     * 移除key过期时间变永久
     *
     * @param key cacheKey
     * @return 执行结果
     */
    Boolean setPermanentByKey(String key);

    /**
     * 分布式锁
     *
     * @param key    cacheKey
     * @param value  data
     * @param nxxx   NX-key不存在时进行保存，XX-key存在时才进行保存
     * @param expire 过期时间
     * @param unit   时间类型
     * @return 执行结果
     */
    Boolean distributedLock(String key, Object value, @NotNull String nxxx, @NotNull long expire, @NotNull TimeUnit unit);

    /**
     * 秒杀锁
     *
     * @param key             cacheKey
     * @param value           data
     * @param lockWaitTimeOut 总持续时间
     * @param unit            时间类型
     * @return 执行结果
     */
    Boolean preemptiveLock(String key, Object value, long lockWaitTimeOut, TimeUnit unit);

    /**
     * 解除锁
     *
     * @param key   cacheKey
     * @param value data
     * @return 执行结果
     */
    Boolean unlock(String key, Object value);

    /**
     * 点赞功能
     *
     * @param key    来源id
     * @param offset 用户id
     * @param value  是否点赞
     * @return 执行结果
     */
    void setBit(String key, long offset, Boolean value);

    /**
     * 获取点赞数量
     *
     * @param key 来源id
     * @return 数量
     */
    Long bitCount(String key);

    /**
     * 增量计算
     *
     * @param key   key
     * @param delta 增加数量
     * @return 当前数量
     */
    Long increment(String key, long delta);

    /**
     * 减量计算
     *
     * @param key   key
     * @param delta 减少数量
     * @return 当前数量
     */
    Long decrement(String key, long delta);

    // zset

    /**
     * 队列形式点赞
     *
     * @param key   队列名称
     * @param value 用户id
     * @param delta 是否点赞
     */
    void zadd(String key, String value, long delta);

    /**
     * 队列形式取消点赞
     *
     * @param key    队列名称
     * @param values 用户id
     * @return 执行结果
     */
    Boolean zrem(String key, String... values);

    /**
     * 队列形式增量计算
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    Boolean zincrby(String key, String value, long delta);

    /**
     * 队列正序
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 列表
     */
    Set<String> zrange(String key, long start, long end);

    /**
     * 队列倒序
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列
     */
    Set<String> zrevrange(String key, long start, long end);

    /**
     * 队列长度
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列长度
     */
    Integer zlexcount(String key, long start, long end);

    /**
     * 正序当前value在队列中的位置
     *
     * @param key   队列名称
     * @param value value
     * @return value位置
     */
    Long zrank(String key, String value);

    /**
     * 倒序当前value在队列中的位置
     *
     * @param key   队列名称
     * @param value value
     * @return value位置
     */
    Long zrevrank(String key, String value);

    /**
     * stream追加消息
     *
     * @param key     队列名称
     * @param message 消息载体
     * @return 返回唯一标识
     */
    String xAdd(@NonNull String key, @NonNull Map<Object, Object> message);

    /**
     * 删除消息
     *
     * @param key       队列名称
     * @param messageId 消息id
     * @return 执行结果
     */
    Boolean xDel(@NonNull String key, @NonNull String messageId);

    /**
     * 正序取出stream查询结果集
     *
     * @param key 队列名称
     * @return 结果集
     */
    List<MapRecord<String, Object, Object>> xRange(@NonNull String key);

    /**
     * 倒序取出stream查询结果集
     *
     * @param key 队列名称
     * @return 结果集
     */
    List<MapRecord<String, Object, Object>> xRevRange(@NonNull String key);

    /**
     * stream长度
     *
     * @param key 队列名称
     * @return 队列长度
     */
    Long xLen(@NonNull String key);

    /**
     * 顺序消费消息(返回的消息为下一次messageId)
     *
     * @param key       队列名称
     * @param count     消息条数
     * @param messageId 消息id
     * @return 执行结果
     */
    List<MapRecord<String, Object, Object>> xRead(@NonNull String key, long count, String messageId);

    /**
     * 创建与已经存的stream的新消费组
     *
     * @param key       队列名称
     * @param group     消费组名称
     * @param messageId 消息id
     * @return 执行结果
     */
    boolean xGroupCreate(@NonNull String key, @NonNull String group, String messageId);

    /**
     * 删除消费组
     *
     * @param key   队列名称
     * @param group 消费组名称
     * @return 执行结果
     */
    boolean xDelGroup(@NonNull String key, @NonNull String group);

    /**
     * 从消费组租读取数据
     *
     * @param group    消费组名称
     * @param consumer 消费组
     * @param key      队列名称
     * @param count    消息条数
     * @return 执行结果
     */
    List<MapRecord<String, Object, Object>> xReadGroup(@NonNull String group, @NonNull String consumer, @NonNull String key, @NonNull long count);

    /**
     * 确认消息，已处理消息列表中删除
     *
     * @param key       队列名称
     * @param group     消费组名称
     * @param messageId 消息id
     * @return 执行结果
     */
    boolean xAck(@NonNull String key, @NonNull String group, @NonNull String messageId);


}
