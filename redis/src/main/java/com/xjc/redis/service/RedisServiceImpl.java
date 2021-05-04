package com.xjc.redis.service;

import com.alibaba.fastjson.JSON;
import com.xjc.redis.api.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName RedisServiceImpl
 * @Author jiachenXu
 * @Date 2020/3/5
 * @Description 缓存工具类
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    private static final String NOT_EXIST = "NX";

    private static final String EXIST = "XX";

    private static final Long ONE_RESULT = 1L;

    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) else return 0 end ";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> stringOperations;

    private ZSetOperations<String, String> zSetOperations;

    private HashOperations<String, Object, Object> hashOperations;

    private StreamOperations<String, Object, Object> streamOperations;

    @PostConstruct
    private void init() {
        stringOperations = redisTemplate.opsForValue();
        zSetOperations = redisTemplate.opsForZSet();
        hashOperations = redisTemplate.opsForHash();
        streamOperations = redisTemplate.opsForStream();
    }

    @Override
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Set<String> scanKey(String pattern, int count) {
        Set<String> keysTmp = new LinkedHashSet<>();
        redisTemplate.execute((RedisCallback<Set<String>>) con -> {
            Cursor<byte[]> cursor = con.scan(new ScanOptions
                    .ScanOptionsBuilder()
                    .match("*" + pattern + "*")
                    .count(count)
                    .build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });
        return keysTmp;
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public Object get(String key) {
        return stringOperations.get(key);
    }

    @Override
    public void set(String key, Object value) {
        stringOperations.set(key, JSON.toJSONString(value));
    }

    @Override
    public Boolean setWithExpire(String key, Object value, long expire, TimeUnit unit) {
        String v = JSON.toJSONString(value);
        return stringOperations.setIfAbsent(key, v, expire, unit);
    }

    @Override
    public Object getKeyWithExpire(String key, long expire, TimeUnit unit) {
        Object value = get(key);
        if (Objects.nonNull(value)) {
            redisTemplate.expire(key, expire, unit);
            return value;
        }
        return null;
    }

    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean renameByKey(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    @Override
    public Boolean setPermanentByKey(String key) {
        return redisTemplate.persist(key);
    }

    @Override
    public Boolean distributedLock(String key, Object value, String nxxx, long expire, TimeUnit unit) {
        try {
            final String v = JSON.toJSONString(value);
            // SET_IF_ABSENT--->NX
            // SET_IF_PRESENT--->XX
            if (!StringUtils.isEmpty(nxxx)) {
                if (NOT_EXIST.equalsIgnoreCase(nxxx)) {
                    return stringOperations.setIfAbsent(key, v, expire, unit);
                } else if (EXIST.equalsIgnoreCase(nxxx)) {
                    return stringOperations.setIfPresent(key, v, expire, unit);
                }
            }
        } catch (Exception e) {
            log.error("cache-distributedLock error", e);
        }
        log.error("cache-distributedLock failed, key=" + key);
        return false;
    }

    @Override
    public Boolean preemptiveLock(String key, Object value, long lockWaitTimeOut, TimeUnit unit) {
        try {
            long deadTimeLine = System.currentTimeMillis() + lockWaitTimeOut;

            for (; ; ) {
                // 循环退出剩余时间，秒杀结束
                if (deadTimeLine - lockWaitTimeOut <= 0L) {
                    return false;
                }
                final String v = JSON.toJSONString(value);
                return stringOperations.setIfAbsent(key, v, lockWaitTimeOut, unit);
            }
        } catch (Exception e) {
            log.error("cache-preemptiveLock error", e);
        }
        return false;
    }

    @Override
    public Boolean unlock(String key, Object value) {
        final String v = JSON.toJSONString(value);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SCRIPT, Long.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(key), v);
        return Objects.equals(result, ONE_RESULT);
    }

    @Override
    public void setBit(String key, long offset, Boolean value) {
        stringOperations.setBit(key, offset, value);
    }

    @Override
    public Long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    @Override
    public Long bitOp(RedisStringCommands.BitOperation bitOperation, List<String> keys, String resultKey) {
        byte[][] bytes = new byte[keys.size()][];
        final int[] index = {0};
        keys.forEach(k -> bytes[index[0]++] = k.getBytes());
        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(bitOperation, resultKey.getBytes(), bytes));
    }

    @Override
    public Long increment(String key, long delta) {
        return stringOperations.increment(key, delta);
    }

    @Override
    public Long decrement(String key, long delta) {
        return stringOperations.decrement(key, delta);
    }

    @Override
    public Long zCard(String key) {
        return zSetOperations.zCard(key);
    }

    @Override
    public void zAdd(String key, String value, long delta) {
        zSetOperations.add(key, value, delta);
    }

    @Override
    public void zRem(String key, List<String> values) {
        zSetOperations.remove(key, values);
    }

    @Override
    public void removeRange(String key, long start, long end) {
        zSetOperations.removeRange(key, start, end);
    }

    @Override
    public void removeRangeByScore(String key, long min, long max) {
        zSetOperations.removeRangeByScore(key, min, max);
    }

    @Override
    public Boolean zincrby(String key, String value, long delta) {
        return zSetOperations.incrementScore(key, value, delta) != null;
    }

    @Override
    public Set<String> zRange(String key, long start, long end) {
        return zSetOperations.range(key, start, end);
    }

    @Override
    public Set<String> zRevRange(String key, long start, long end) {
        return zSetOperations.reverseRange(key, start, end);
    }

    @Override
    public Set<String> zRangeByScore(String key, long min, long max) {
        return zSetOperations.rangeByScore(key, min, max);
    }

    @Override
    public Set<String> zReverseRangeByScore(String key, long min, long max) {
        return zSetOperations.reverseRangeByScore(key, min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, long start, long end) {
        return zSetOperations.reverseRangeWithScores(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, long start, long end) {
        return zSetOperations.rangeWithScores(key, start, end);
    }

    @Override
    public Long zLexCount(String key, long start, long end) {
        return zSetOperations.count(key, start, end);
    }

    @Override
    public Long zRank(String key, String value) {
        return zSetOperations.rank(key, value);
    }

    @Override
    public Long zRevRank(String key, String value) {
        return zSetOperations.reverseRank(key, value);
    }

    @Override
    public Boolean hasKey(String key, String hashKey) {
        return hashOperations.hasKey(key, hashKey);
    }

    @Override
    public List<Object> hashKeys(String key) {
        return Collections.singletonList(hashOperations.keys(key));
    }

    @Override
    public Boolean hashPutIfAbsent(String key, Pair<Object, Object> pair) {
        return hashOperations.putIfAbsent(key, pair.getLeft(), pair.getRight());
    }

    @Override
    public void hashPutAll(String key, Map<Object, Object> map) {
        hashOperations.putAll(key, map);
    }

    @Override
    public Boolean hashExpire(String key, long expire, TimeUnit timeUnit) {
        return hashOperations.getOperations().expire(key, expire, timeUnit);
    }

    @Override
    public Map<Object, Object> hGetAllKey(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public Object hashGet(String key, String hashKey) {
        return hashOperations.get(key, hashKey);
    }

    @Override
    public List<Object> hashMultiGetByHashKey(String key, List<String> hashKeys) {
        return hashOperations.multiGet(key, Collections.singleton(hashKeys));
    }

    @Override
    public Map<Object, Pair<Object, Object>> hashGetAll(List<String> keys) {
        Map<Object, Pair<Object, Object>> result = Maps.newLinkedHashMap();
        keys.forEach(key -> {
            Map<Object, Object> objectMap = hGetAllKey(key);
            objectMap.forEach((k, v) -> {
                result.put(key, ImmutablePair.of(k, v));
            });
        });
        return result;
    }

    @Override
    public void hashDelete(String key, List<String> hashKey) {
        hashOperations.delete(key, hashKey);
    }

    @Override
    public Long hashSize(String key) {
        return hashOperations.size(key);
    }

    @Override
    public Long lengthOfValue(String key, String hashKey) {
        return hashOperations.lengthOfValue(key, hashKey);
    }

    @Override
    public Long hashIncrement(String key, String hashKey, long delta) {
        return hashOperations.increment(key, hashKey, delta);
    }

    @Override
    public String xAdd(String key, Map<Object, Object> message) {
        RecordId recordId = streamOperations.add(key, message);
        if (Objects.nonNull(recordId)) {
            if (recordId.shouldBeAutoGenerated()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(recordId.getTimestamp()).append("-").append(recordId.getSequence());
                return stringBuilder.toString();
            }
        }
        return null;
    }

    @Override
    public boolean xDel(String key, String messageId) {
        return streamOperations.delete(key, messageId) != null;
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRange(String key) {
        return streamOperations.range(key, Range.unbounded());
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRevRange(String key) {
        return streamOperations.reverseRange(key, Range.unbounded());
    }

    @Override
    public Long xLen(String key) {
        return streamOperations.size(key);
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRead(String key, long count, String messageId) {
        List<MapRecord<String, Object, Object>> recordList;
        if (Objects.isNull(count)) {
            count = 1L;
        }
        if (StringUtils.isEmpty(messageId)) {
            recordList = streamOperations.read(StreamReadOptions.empty().count(count));
        } else {
            String[] messageIdArray = convertMessageId(messageId);
            long millisecondsTime = Long.parseLong(messageIdArray[0]);
            long sequenceNumber = Long.parseLong(messageIdArray[1]);
            recordList = streamOperations.read(StreamReadOptions.empty().count(count),
                    StreamOffset.create(key, ReadOffset.from(RecordId.of(millisecondsTime, sequenceNumber))));
        }

        return recordList;
    }

    @Override
    public Boolean xGroupCreate(String key, String group, String messageId) {
        Boolean result;
        if (StringUtils.isEmpty(messageId)) {
            result = streamOperations.createGroup(key, ReadOffset.latest(), group) != null;
        } else {
            String[] messageIdArray = convertMessageId(messageId);
            long millisecondsTime = Long.parseLong(messageIdArray[0]);
            long sequenceNumber = Long.parseLong(messageIdArray[1]);
            result = streamOperations.createGroup(key,
                    ReadOffset.from(RecordId.of(millisecondsTime, sequenceNumber)), group) != null;
        }
        return result;
    }

    @Override
    public Boolean xDelGroup(String key, String group) {
        return streamOperations.destroyGroup(key, group);
    }

    @Override
    public List<MapRecord<String, Object, Object>> xReadGroup(String group, String consumer, String key, long count) {
        if (Objects.isNull(count)) {
            count = 1L;
        }
        return streamOperations.read(Consumer.from(group, consumer),
                StreamReadOptions.empty().count(count), StreamOffset.create(key, ReadOffset.lastConsumed()));
    }

    @Override
    public Boolean xAck(String key, String group, String messageId) {
        String[] messageIdArray = convertMessageId(messageId);
        long millisecondsTime = Long.parseLong(messageIdArray[0]);
        long sequenceNumber = Long.parseLong(messageIdArray[1]);
        return streamOperations.acknowledge(key, group, RecordId.of(millisecondsTime, sequenceNumber)) != null;
    }

    private String[] convertMessageId(String messageId) {
        return messageId.split("-");
    }

}