package com.xjc.redis.service;

import com.alibaba.fastjson.JSON;
import com.xjc.redis.api.RedisService;
import lombok.extern.slf4j.Slf4j;
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
@SuppressWarnings("all")
public class RedisServiceImpl implements RedisService {

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
    public boolean exists(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (RuntimeException e) {
            log.error("cache-get error, key=" + key);
        }
        return false;
    }

    @Override
    public Set<String> scanKey(String pattern, int count) {
        Set<String> keysTmp = new LinkedHashSet<>( );
        stringRedisTemplate.execute((RedisCallback<Set<String>>) con -> {
            Cursor<byte[]> cursor = con.scan(new ScanOptions
                    .ScanOptionsBuilder( )
                    .match("*" + pattern + "*")
                    .count(count)
                    .build( ));
            while (cursor.hasNext( )) {
                keysTmp.add(new String(cursor.next( )));
            }
            return keysTmp;
        });
        return keysTmp;
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
    public boolean setWithExpire(String key, Object value, long expire, TimeUnit unit) {
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
    public boolean delete(String key) {
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
    public boolean renameByKey(String oldKey, String newKey) {
        if (stringRedisTemplate.renameIfAbsent(oldKey, newKey)) {
            return true;
        }
        log.error("cache-distributedLock error");
        return false;

    }

    @Override
    public boolean setPermanentByKey(String key) {
        if (exists(key)) {
            return stringRedisTemplate.persist(key);
        }
        return false;
    }

    @Override
    public boolean distributedLock(String key, Object value, String nxxx, long expire, TimeUnit unit) {
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
    public boolean preemptiveLock(String key, Object value, long lockWaitTimeOut, TimeUnit unit) {
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
    public boolean unlock(String key, Object value) {
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
    public long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes( )));
    }

    @Override
    public long bitOp(RedisStringCommands.BitOperation bitOperation, List<String> keys, String resultKey) {
        byte[][] bytes = new byte[keys.size( )][];
        final int[] index = {0};
        keys.forEach(k -> bytes[index[0]++] = k.getBytes( ));
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(bitOperation, resultKey.getBytes( ), bytes));
    }

    @Override
    public long increment(String key, long delta) {
        return stringOperations.increment(key, delta);
    }

    @Override
    public long decrement(String key, long delta) {
        return stringOperations.decrement(key, delta);
    }

    @Override
    public long zCard(String key) {
        return zSetOperations.zCard(key);
    }

    @Override
    public void zAdd(String key, String value, long delta) {
        zSetOperations.add(key, value, delta);
    }

    @Override
    public boolean zRem(String key, List<String> values) {
        return zSetOperations.remove(key, values.toArray(new String[0])) == 1;
    }

    @Override
    public boolean zincrby(String key, String value, long delta) {
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
    public Set<String> zRangeByScore(String key, long start, long end) {
        return zSetOperations.rangeByScore(key, start, end);
    }

    @Override
    public Set<String> zReverseRangeByScore(String key, long start, long end) {
        return zSetOperations.reverseRangeByScore(key, start, end);
    }

    @Override
    public long zlexCount(String key, long start, long end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return zRevRange(key, 0, -1).size( );
        }
        return Integer.parseInt(zSetOperations.count(key, start, end).toString( ));
    }

    @Override
    public long zRank(String key, String value) {
        return zSetOperations.rank(key, value);
    }

    @Override
    public long zRevRank(String key, String value) {
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
    public boolean xDel(String key, String messageId) {
        return streamOperations.delete(key, messageId) != null;
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRange(String key) {
        return streamOperations.range(key, Range.unbounded( ));
    }

    @Override
    public List<MapRecord<String, Object, Object>> xRevRange(String key) {
        return streamOperations.reverseRange(key, Range.unbounded( ));
    }

    @Override
    public long xLen(String key) {
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