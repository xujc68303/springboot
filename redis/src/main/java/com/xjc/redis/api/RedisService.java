package com.xjc.redis.api;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.geo.Point;
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
    Set<String> scanKey(String pattern, Integer count);

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
    Boolean setWithExpire(String key, Object value, Long expire, TimeUnit unit);

    /**
     * 给Key重新设置过期时间并获取Value
     *
     * @param key    cacheKey
     * @param expire 过期时间
     * @param unit   时间类型
     * @return Value
     */
    Object getKeyWithExpire(String key, Long expire, TimeUnit unit);

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
    Boolean distributedLock(String key, Object value, String nxxx, Long expire, TimeUnit unit);

    /**
     * 秒杀锁
     *
     * @param key             cacheKey
     * @param value           data
     * @param lockWaitTimeOut 总持续时间
     * @param unit            时间类型
     * @return 执行结果
     */
    Boolean preemptiveLock(String key, Object value, Long lockWaitTimeOut, TimeUnit unit);

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
    void setBit(String key, Long offset, Boolean value);

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
    void zAdd(String key, String value, Long delta);

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
    void removeRange(String key, Long start, Long end);

    /**
     * 根据score最小到最大删除队列中的元素
     *
     * @param key 队列名称
     * @param min 最小下标
     * @param max 最大下标
     */
    void removeRangeByScore(String key, Long min, Long max);

    /**
     * 队列增量计算
     *
     * @param key   队列名称
     * @param value 元素
     * @param delta 增量值
     * @return 执行结果
     */
    Boolean zincrby(String key, String value, Long delta);

    /**
     * 根据下标位置正序显示
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 列表
     */
    Set<String> zRange(String key, Long start, Long end);

    /**
     * 根据下标位置倒序显示
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列
     */
    Set<String> zRevRange(String key, Long start, Long end);

    /**
     * 根据score的大小升序显示value
     *
     * @param key 队列名称
     * @param min 开始位置
     * @param max 结束位置
     * @return 队列
     */
    Set<String> zRangeByScore(String key, Long min, Long max);

    /**
     * 根据score的大小倒序显示value
     *
     * @param key 队列名称
     * @param min 最小值
     * @param max 最大值
     * @return 队列
     */
    Set<String> zReverseRangeByScore(String key, Long min, Long max);

    /**
     * 根据下标位置倒序显示全部信息
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, Long start, Long end);

    /**
     * 根据下标位置正序显示全部信息
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return
     */
    Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, Long start, Long end);

    /**
     * 队列长度
     *
     * @param key   队列名称
     * @param start 开始位置
     * @param end   结束位置
     * @return 队列长度
     */
    Long zLexCount(String key, Long start, Long end);

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
     * 获取所有哈希表中的hashKey
     *
     * @param key key
     * @return hashKey
     */
    List<Object> hashKeys(String key);

    /**
     * 只有在hashKey不存在时设置值
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
    Boolean hashExpire(String key, Long expire, TimeUnit timeUnit);

    /**
     * 获取在哈希表中指定key的所有字段和值
     *
     * @param key key
     * @return Pair
     */
    Map<Object, Object> hGetAllKey(String key);

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

    /**
     * 哈希增减量
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   量值
     * @return 当前hashKey量值
     */
    Long hashIncrement(String key, String hashKey, Long delta);

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
    List<MapRecord<String, Object, Object>> xRead(String key, Long count, String messageId);

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
    List<MapRecord<String, Object, Object>> xReadGroup(String group, String consumer, String key, Long count);

    /**
     * 确认消息，已处理消息列表中删除
     *
     * @param key       队列名称
     * @param group     消费组名称
     * @param messageId 消息id
     * @return 执行结果
     */
    Boolean xAck(String key, String group, String messageId);

    // set

    /**
     * 计算集合的数量
     *
     * @param key 集合名称
     * @return 数量
     */
    Long sCard(String key);

    /**
     * 向集合添加一个值
     *
     * @param key   集合名称
     * @param value 值
     * @return 执行结果
     */
    Long sAdd(String key, String value);

    /**
     * 删除集合中的值
     *
     * @param key   集合名称
     * @param value 值
     * @return 执行结果
     */
    Long sRem(String key, String value);

    /**
     * 是否是集合的成员
     *
     * @param key   集合名称
     * @param value 值
     * @return 执行结果
     */
    Boolean sMember(String key, String value);

    /**
     * 返回集合中的所有值
     *
     * @param key 集合名称
     * @return 值
     */
    Set<String> sMembers(String key);

    /**
     * 返回两个集合的差集
     *
     * @param key1 集合1
     * @param key2 集合2
     * @return 差集结果
     */
    Set<String> sDiff(String key1, String key2);

    /**
     * 对比两个集合的差集并存储
     *
     * @param key1    集合1
     * @param key2    集合2
     * @param destKey 存储集合
     * @return 执行结果
     */
    Long sDiffStore(String key1, String key2, String destKey);

    /**
     * 返回两个集合的并集
     *
     * @param key1 集合1
     * @param key2 集合2
     * @return 并集结果
     */
    Set<String> sUnion(String key1, String key2);

    /**
     * 对比两个集合的并集并存储
     *
     * @param key1    集合1
     * @param key2    集合2
     * @param destKey 存储集合
     * @return 执行结果
     */
    Long sUnionStore(String key1, String key2, String destKey);

    /**
     * 返回集合中指定数量随机值
     *
     * @param key   集合名称
     * @param count 数量
     * @return 随机值
     */
    List<String> sRandomMember(String key, Integer count);

    /**
     * 移除并返回集合中的随机值
     *
     * @param key 集合名称
     * @return 随机值
     */
    String sPop(String key);

    /**
     * 将集合中的数值移动到dest集合
     *
     * @param key     集合名称
     * @param value   数值
     * @param destKey 移动集合
     * @return 执行结果
     */
    Boolean sMove(String key, String value, String destKey);

    // geo

    /**
     * 添加地理位置的坐标
     *
     * @param key       key
     * @param member    位置名称
     * @param longitude 经度
     * @param latitude  纬度
     * @return key长度
     */
    Long geoAdd(String key, String member, Double longitude, Double latitude);

    /**
     * 批量添加地理位置的坐标
     *
     * @param key key
     * @param map 经度纬度
     * @return
     */
    Long geoAdd(String key, Map<String, Point> map);

    /**
     * 获取地理位置的坐标
     *
     * @param key    key
     * @param member 位置名称
     * @return 经度纬度
     */
    Point geoPos(String key, String member);

    /**
     * 获取多个地理位置的坐标
     *
     * @param key     key
     * @param members 位置集合
     * @return 经度纬度
     */
    Map<String, Point> geoPos(String key, List<String> members);

    /**
     * 计算两个位置之间的距离
     *
     * @param key     key
     * @param member1 位置1
     * @param member2 位置2
     * @return 距离
     */
    Double geoDist(String key, String member1, String member2);

    /**
     * 根据储存在位置集合里面的某个地点获取指定范围内的地理位置集合
     *
     * @param key    key
     * @param member 位置
     * @param radius 距离
     * @return 经度纬度
     */
    Map<String, Point> geoRadiusByMember(String key, String member, Double radius);
}
