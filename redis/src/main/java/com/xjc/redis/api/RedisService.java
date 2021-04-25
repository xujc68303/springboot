package com.xjc.redis.api;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName RedisService
 * @Author jiachenXu
 * @Date 2020/3/5
 * @Description 缓存工具类
 */
public interface RedisService {

    /**
     * 检查key是否存在
     *
     * @param key cacheKey
     * @return 执行结果
     */
    Boolean exists(String key);

    /**
     * 查询key
     *
     * @param pattern
     * @param count
     * @return
     */
    Set<String> scanKey(String pattern, int count);

    /**
     * 获取key的过期时间
     *
     * @param key cacheKey
     * @return 过期时间
     */
    Long getExpire(String key);

    // string

    /**
     * 根据Key获取value（Object）
     *
     * @param key cacheKey
     * @return value
     */
    Object get(String key);

    /**
     * 添加key & Data
     *
     * @param key   cacheKey
     * @param value data
     */
    void set(String key, Object value);

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
     * 根据key删除
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
     * @return 执行结果
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
    Boolean distributedLock(String key, Object value, String nxxx, long expire, TimeUnit unit);

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
     * 位图计算
     *
     * @param bitOperation bitOperation
     * @param keys         keys
     * @param resultKey    resultKey
     * @return
     */
    Long bitOp(RedisStringCommands.BitOperation bitOperation, List<String> keys, String resultKey);

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
     * 获取队列的长度
     *
     * @param key 队列名称
     * @return
     */
    Long zCard(String key);

    /**
     * 添加元素入队列
     *
     * @param key   队列名称
     * @param value 元素
     * @param delta 数值
     */
    void zAdd(String key, String value, long delta);

    /**
     * 删除队列中的元素
     *
     * @param key    队列名称
     * @param values 元素
     */
    void zRem(String key, List<String> values);

    /**
     * 根据下标删除队列元素
     *
     * @param key   队列名称
     * @param start 开始下标
     * @param end   结束下标
     */
    void removeRange(String key, long start, long end);

    /**
     * 根据score最小到最大删除队列中的元素
     *
     * @param key 队列名称
     * @param min 最小下标
     * @param max 最大下标
     */
    void removeRangeByScore(String key, long min, long max);

    /**
     * 队列增量计算
     *
     * @param key   队列名称
     * @param value 元素
     * @param delta 增量值
     * @return 执行结果
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
    Set<String> zRange(String key, long start, long end);

    /**
     * 队列倒序
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列
     */
    Set<String> zRevRange(String key, long start, long end);

    /**
     * 根据score的大小升序显示value
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列
     */
    Set<String> zRangeByScore(String key, long start, long end);

    /**
     * 根据score的大小倒序显示value
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列
     */
    Set<String> zReverseRangeByScore(String key, long start, long end);

    /**
     * 从开始到结束，从排序从高到低的排序集中获取元组的集合
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, long start, long end);

    /**
     * 从开始到结束，从排序从低到高的排序集中获取元组的集合
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, long start, long end);

    /**
     * 队列长度
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列长度
     */
    Long zlexCount(String key, long start, long end);

    /**
     * 正序当前value在队列中的位置
     *
     * @param key   队列名称
     * @param value value
     * @return value位置
     */
    Long zRank(String key, String value);

    /**
     * 倒序当前value在队列中的位置
     *
     * @param key   队列名称
     * @param value value
     * @return value位置
     */
    Long zRevRank(String key, String value);

    // hash

    /**
     * hashKey是否存在
     *
     * @param key     key
     * @param hashKey hashKey
     * @return 执行结果
     */
    Boolean hasKey(String key, String hashKey);

    /**
     * 获取映射的hashKey
     *
     * @param key key
     * @return hashKey
     */
    List<Object> hashKeys(String key);

    /**
     * 当key不存在时，缓存数据
     *
     * @param key  key
     * @param pair pair
     * @return 执行结果
     */
    Boolean hashPutIfAbsent(String key, Pair<Object, Object> pair);

    /**
     * 批量缓存数据
     *
     * @param key key
     * @param map map
     */
    void hashPutAll(String key, Map<Object, Object> map);

    /**
     * 给key设置过期时间
     *
     * @param key      key
     * @param expire   过期时间
     * @param timeUnit 时间单位
     * @return 执行结果
     */
    Boolean hashExpire(String key, long expire, TimeUnit timeUnit);

    /**
     * 获取全部map
     *
     * @param key key
     * @return Pair
     */
    Map<Object, Object> hashGetByHashKeys(String key);

    /**
     * 获取hashKey对应value
     *
     * @param key     key
     * @param hashKey hashKey
     * @return value
     */
    Object hashGet(String key, String hashKey);

    /**
     * 根据hashKeys获取value
     *
     * @param key      key
     * @param hashKeys hashKeys
     * @return value
     */
    List<Object> hashMultiGetByHashKey(String key, List<String> hashKeys);

    /**
     * 根据key批量获取
     *
     * @param keys keys
     * @return maps
     */
    Map<Object, Pair<Object, Object>> hashGetAll(List<String> keys);

    /**
     * 根据key删除
     *
     * @param key      key
     * @param hashKeys hashKeys
     * @return 执行结果
     */
    void hashDelete(String key, List<String> hashKeys);

    /**
     * 获取hashKey数量
     *
     * @param key key
     * @return 数量
     */
    Long hashSize(String key);

    /**
     * 返回hashKey映射值的长度
     *
     * @param key     key
     * @param hashKey hashKey
     * @return value长度
     */
    Long lengthOfValue(String key, String hashKey);

    // stream

    /**
     * stream 追加消息
     *
     * @param key     队列名称
     * @param message 消息载体
     * @return 返回唯一标识
     */
    String xAdd(String key, Map<Object, Object> message);

    /**
     * 删除消息
     *
     * @param key       队列名称
     * @param messageId 消息id
     * @return 执行结果
     */
    boolean xDel(String key, String messageId);

    /**
     * 正序取出stream查询结果集
     *
     * @param key 队列名称
     * @return 结果集
     */
    List<MapRecord<String, Object, Object>> xRange(String key);

    /**
     * 倒序取出stream查询结果集
     *
     * @param key 队列名称
     * @return 结果集
     */
    List<MapRecord<String, Object, Object>> xRevRange(String key);

    /**
     * stream长度
     *
     * @param key 队列名称
     * @return 队列长度
     */
    Long xLen(String key);

    /**
     * 顺序消费消息(返回的消息为下一次messageId)
     *
     * @param key       队列名称
     * @param count     消息条数
     * @param messageId 消息id
     * @return 执行结果
     */
    List<MapRecord<String, Object, Object>> xRead(String key, long count, String messageId);

    /**
     * 创建与已经存的stream的新消费组
     *
     * @param key       队列名称
     * @param group     消费组名称
     * @param messageId 消息id
     * @return 执行结果
     */
    Boolean xGroupCreate(String key, String group, String messageId);

    /**
     * 删除消费组
     *
     * @param key   队列名称
     * @param group 消费组名称
     * @return 执行结果
     */
    Boolean xDelGroup(String key, String group);

    /**
     * 从消费组租读取数据
     *
     * @param group    消费组名称
     * @param consumer 消费组
     * @param key      队列名称
     * @param count    消息条数
     * @return 执行结果
     */
    List<MapRecord<String, Object, Object>> xReadGroup(String group, String consumer, String key, long count);

    /**
     * 确认消息，已处理消息列表中删除
     *
     * @param key       队列名称
     * @param group     消费组名称
     * @param messageId 消息id
     * @return 执行结果
     */
    Boolean xAck(String key, String group, String messageId);

}
