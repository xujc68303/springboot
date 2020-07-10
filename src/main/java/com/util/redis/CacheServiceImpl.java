package com.util.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName CacheServiceImpl
 * @Author jiachenXu
 * @Date 2020/3/5 13:41
 * @Description 缓存工具类
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class CacheServiceImpl implements CacheService {

    private static final String NOT_EXIST = "NX";

    private static final String EXIST = "XX";

    private static final Long ONE_RESULT = 1L;

    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) else return 0 end ";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> stringOperations;

    private ZSetOperations<String, String> zSetOperations;

    private StreamOperations<String, Object, Object> streamOperations;

    @PostConstruct
    private void init() {
        stringOperations = stringRedisTemplate.opsForValue( );
        zSetOperations = stringRedisTemplate.opsForZSet( );
        streamOperations = stringRedisTemplate.opsForStream( );
    }

    @Override
    public Boolean exists(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (RuntimeException e) {
            log.error("cache-get error, key=" + key);
        }
        return false;
    }

    @Override
    public Object get(String key) {
        try {
            return stringOperations.get(key);
        } catch (RuntimeException e) {
            log.error("cache-get error, key=" + key);
        }
        return null;
    }

    @Override
    public Boolean setWithExpire(String key, Object value, long expire, TimeUnit unit) {
        try {
            String v = JSON.toJSONString(value);
            return stringOperations.setIfAbsent(key, v, expire, unit);
        } catch (RuntimeException e) {
            log.error("cache-setexWithExpire error", e);
        }
        log.error("cache-setexWithExpire failed, key=" + key);
        return false;

    }

    @Override
    public Object getKeyWithExpire(String key, long expire, TimeUnit unit) {
        try {
            Object value = get(key);
            if (Objects.nonNull(value)) {
                return stringRedisTemplate.expire(key, expire, unit);
            }
        } catch (RuntimeException e) {
            log.error("cache-getKeyWithExpire error, key=" + key, e);
        }
        log.error("cache-getKeyWithExpire failed, key=" + key);
        return null;
    }

    @Override
    public Boolean delete(String key) {
        try {
            if (exists(key)) {
                return stringRedisTemplate.delete(key);
            }
        } catch (RuntimeException e) {
            log.error("cache-delete error", e);
        }
        log.error("cache-delete failed, key=" + key);
        return false;
    }

    @Override
    public Boolean renameByKey(String oldKey, String newKey) {
        if (stringRedisTemplate.renameIfAbsent(oldKey, newKey)) {
            return true;
        }
        log.error("cache-distributedLock error");
        return false;

    }

    @Override
    public Boolean setPermanentByKey(String key) {
        if (exists(key)) {
            return stringRedisTemplate.persist(key);
        }
        return false;
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
            long deadTimeLine = System.currentTimeMillis( ) + lockWaitTimeOut;

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
        try {
            final String v = JSON.toJSONString(value);
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SCRIPT, Long.class);
            Object result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), v);
            return Objects.equals(result, ONE_RESULT);
        } catch (Exception e) {
            log.error("cache-unlock error, key=" + key, e);
        }
        log.error("cache-unlock failed, key=" + key);
        return false;
    }

    @Override
    public void setBit(String key, long offset, Boolean value) {
        stringOperations.setBit(key, offset, value);
    }

    @Override
    public Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes( )));
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
    public void zadd(String key, String value, long delta) {
        zSetOperations.add(key, value, delta);
    }

    @Override
    public Boolean zrem(String key, String... value) {
        return zSetOperations.remove(key, value) == 1;
    }

    @Override
    public Boolean zincrby(String key, String value, long delta) {
        return zSetOperations.incrementScore(key, value, delta) != null;
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return zSetOperations.range(key, start, end);
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        return zSetOperations.reverseRange(key, start, end);
    }

    @Override
    public Integer zlexcount(String key, long start, long end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return zrevrange(key, 0, -1).size( );
        }
        return Integer.parseInt(zSetOperations.count(key, start, end).toString( ));
    }

    @Override
    public Long zrank(String key, String value) {
        return zSetOperations.rank(key, value);
    }

    @Override
    public Long zrevrank(String key, String value) {
        return zSetOperations.reverseRank(key, value);
    }

    @Override
    public String xAdd(String key, Map<Object, Object> message) {
        RecordId recordId = streamOperations.add(key, message);
        if (Objects.nonNull(recordId)) {
            if (recordId.shouldBeAutoGenerated( )) {
                StringBuilder stringBuilder = new StringBuilder( );
                stringBuilder.append(recordId.getTimestamp( )).append("-").append(recordId.getSequence( ));
                return stringBuilder.toString( );
            }
        }
        return null;
    }

    @Override
    public Boolean xDel(String key, String messageId) {
        return streamOperations.delete(key, messageId) != null;
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRange(@NonNull String key) {
        return streamOperations.range(key, Range.unbounded( ));
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRevRange(String key) {
        return streamOperations.reverseRange(key, Range.unbounded( ));
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
            recordList = streamOperations.read(StreamReadOptions.empty( ).count(count));
        } else {
            String[] messageIdArray = convertMessageId(messageId);
            long millisecondsTime = Long.parseLong(messageIdArray[0]);
            long sequenceNumber = Long.parseLong(messageIdArray[1]);
            recordList = streamOperations.read(StreamReadOptions.empty( ).count(count),
                    StreamOffset.create(key, ReadOffset.from(RecordId.of(millisecondsTime, sequenceNumber))));
        }

        return recordList;
    }

    @Override
    public boolean xGroupCreate(String key, String group, String messageId) {
        boolean result;
        if (StringUtils.isEmpty(messageId)) {
            result = streamOperations.createGroup(key, ReadOffset.latest( ), group) != null;
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
    public boolean xDelGroup(String key, String group) {
        return streamOperations.destroyGroup(key, group);
    }

    @Override
    public List<MapRecord<String, Object, Object>> xReadGroup(String group, String consumer, String key, long count) {
        if (Objects.isNull(count)) {
            count = 1L;
        }
        return streamOperations.read(Consumer.from(group, consumer),
                StreamReadOptions.empty( ).count(count), StreamOffset.create(key, ReadOffset.lastConsumed( )));
    }

    @Override
    public boolean xAck(String key, String group, String messageId) {
        String[] messageIdArray = convertMessageId(messageId);
        long millisecondsTime = Long.parseLong(messageIdArray[0]);
        long sequenceNumber = Long.parseLong(messageIdArray[1]);
        return streamOperations.acknowledge(key, group, RecordId.of(millisecondsTime, sequenceNumber)) != null;
    }

    private String[] convertMessageId(String messageId) {
        return messageId.split("-");
    }

}