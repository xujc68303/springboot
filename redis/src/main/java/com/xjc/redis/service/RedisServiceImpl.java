package com.xjc.redis.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.xjc.redis.api.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> stringOperations;
    private ZSetOperations<String, String> zSetOperations;
    private HashOperations<String, Object, Object> hashOperations;
    private StreamOperations<String, Object, Object> streamOperations;
    private SetOperations<String, String> setOperations;
    private GeoOperations<String, String> geoOperations;
    private ListOperations<String, String> listOperations;
    private HyperLogLogOperations<String, String> hyperLogLogOperations;

    @PostConstruct
    private void init() {
        stringOperations = stringRedisTemplate.opsForValue();
        zSetOperations = stringRedisTemplate.opsForZSet();
        hashOperations = stringRedisTemplate.opsForHash();
        streamOperations = stringRedisTemplate.opsForStream();
        setOperations = stringRedisTemplate.opsForSet();
        geoOperations = stringRedisTemplate.opsForGeo();
        listOperations = stringRedisTemplate.opsForList();
        hyperLogLogOperations = stringRedisTemplate.opsForHyperLogLog();
    }

    @Override
    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void unLink(List<String> key) {
        stringRedisTemplate.unlink(key);
    }

    @Override
    public Set<String> scanKey(String pattern, Integer count) {
        Set<String> keysTmp = new LinkedHashSet<>();
        stringRedisTemplate.execute((RedisCallback<Set<String>>) con -> {
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
        return stringRedisTemplate.getExpire(key);
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
    public Boolean setWithExpire(String key, Object value, Long expire, TimeUnit unit) {
        return stringOperations.setIfAbsent(key, JSON.toJSONString(value), expire, unit);
    }

    @Override
    public Object getKeyWithExpire(String key, Long expire, TimeUnit unit) {
        Object value = get(key);
        if (Objects.nonNull(value)) {
            stringRedisTemplate.expire(key, expire, unit);
            return value;
        }
        return null;
    }

    @Override
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    @Override
    public Boolean renameByKey(String oldKey, String newKey) {
        return stringRedisTemplate.renameIfAbsent(oldKey, newKey);
    }

    @Override
    public Boolean setPermanentByKey(String key) {
        return stringRedisTemplate.persist(key);
    }

    @Override
    public Boolean distributedLock(String key, Object value, String nxxx, Long expire, TimeUnit unit) {
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
    public Boolean preemptiveLock(String key, Object value, Long lockWaitTimeOut, TimeUnit unit) {
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
        String v = JSON.toJSONString(value);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SCRIPT, Long.class);
        Object result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), v);
        return Objects.equals(result, ONE_RESULT);
    }

    @Override
    public void setBit(String key, Long offset, Boolean value) {
        stringOperations.setBit(key, offset, value);
    }

    @Override
    public Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    @Override
    public Long bitOp(RedisStringCommands.BitOperation bitOperation, List<String> keys, String resultKey) {
        byte[][] bytes = new byte[keys.size()][];
        final int[] index = {0};
        keys.forEach(k -> bytes[index[0]++] = k.getBytes());
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(bitOperation, resultKey.getBytes(), bytes));
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
    public void zAdd(String key, String value, Long score) {
        zSetOperations.add(key, value, score);
    }

    @Override
    public void zRem(String key, List<String> values) {
        zSetOperations.remove(key, values);
    }

    @Override
    public void removeRange(String key, Long start, Long end) {
        zSetOperations.removeRange(key, start, end);
    }

    @Override
    public void removeRangeByScore(String key, Long min, Long max) {
        zSetOperations.removeRangeByScore(key, min, max);
    }

    @Override
    public Boolean zincrby(String key, String value, Long delta) {
        return zSetOperations.incrementScore(key, value, delta) != null;
    }

    @Override
    public Set<String> zRange(String key, Long start, Long end) {
        return zSetOperations.range(key, start, end);
    }

    @Override
    public Set<String> zRevRange(String key, Long start, Long end) {
        return zSetOperations.reverseRange(key, start, end);
    }

    @Override
    public Set<String> zRangeByScore(String key, Long min, Long max) {
        return zSetOperations.rangeByScore(key, min, max);
    }

    @Override
    public Set<String> zReverseRangeByScore(String key, Long min, Long max) {
        return zSetOperations.reverseRangeByScore(key, min, max);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(String key, Long start, Long end) {
        return zSetOperations.reverseRangeWithScores(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> rangeWithScores(String key, Long start, Long end) {
        return zSetOperations.rangeWithScores(key, start, end);
    }

    @Override
    public Long zLexCount(String key, Long start, Long end) {
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
    public Boolean hashExpire(String key, Long expire, TimeUnit timeUnit) {
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
    public Long hashIncrement(String key, String hashKey, Long delta) {
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
    public List<MapRecord<String, Object, Object>> xRead(String key, Long count, String messageId) {
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
    public List<MapRecord<String, Object, Object>> xReadGroup(String group, String consumer, String key, Long count) {
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

    @Override
    public Long sCard(String key) {
        return setOperations.size(key);
    }

    @Override
    public Long sAdd(String key, String value) {
        return sAdd(key, Sets.newHashSet(value));
    }

    public Long sAdd(String key, Set<String> set) {
        return setOperations.add(key, set.toArray(new String[0]));
    }

    @Override
    public Long sRem(String key, String value) {
        return setOperations.remove(key, value);
    }

    @Override
    public Boolean sMember(String key, String value) {
        return setOperations.isMember(key, value);
    }

    @Override
    public Set<String> sMembers(String key) {
        return setOperations.members(key);
    }

    @Override
    public Set<String> sDiff(String key1, String key2) {
        return setOperations.difference(key1, key2);
    }

    @Override
    public Long sDiffStore(String key1, String key2, String destKe) {
        return setOperations.differenceAndStore(key1, key2, destKe);
    }

    @Override
    public Set<String> sUnion(String key1, String key2) {
        return setOperations.union(key1, key2);
    }

    @Override
    public Long sUnionStore(String key1, String key2, String destKey) {
        return setOperations.unionAndStore(key1, key2, destKey);
    }

    @Override
    public List<String> sRandomMember(String key, Integer count) {
        return setOperations.randomMembers(key, count);
    }

    @Override
    public String sPop(String key) {
        return setOperations.pop(key);
    }

    @Override
    public Boolean sMove(String key, String value, String destKey) {
        return setOperations.move(key, value, destKey);
    }

    @Override
    public Long geoAdd(String key, String member, Double longitude, Double latitude) {
        Map<String, Point> memberCoordinateMap = new HashMap<>();
        memberCoordinateMap.put(member, new Point(longitude, latitude));
        return geoAdd(key, memberCoordinateMap);
    }

    @Override
    public Long geoAdd(String key, Map<String, Point> map) {
        return geoOperations.add(key, map);
    }

    @Override
    public Point geoPos(String key, String member) {
        List<Point> position = geoOperations.position(key, member);
        if (!CollectionUtils.isEmpty(position)) {
            return position.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Point> geoPos(String key, List<String> members) {
        Map<String, Point> map = new HashMap<>();
        for (String member : members) {
            Point point = geoPos(key, member);
            if (Objects.nonNull(point)) {
                map.put(member, point);
            }
        }
        return map;
    }

    @Override
    public Double geoDist(String key, String member1, String member2) {
        Distance distance = geoOperations.distance(key, member1, member2);
        if (Objects.nonNull(distance)) {
            return distance.getValue();
        }
        return null;
    }

    @Override
    public Map<String, Point> geoRadiusByMember(String key, String member, Double radius) {
        Map<String, Point> map = new HashMap<>();
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = geoOperations.radius(key, member, radius);
        if (Objects.nonNull(geoResults)) {
            List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = geoResults.getContent();
            content.forEach(x -> {
                RedisGeoCommands.GeoLocation<String> xContent = x.getContent();
                String name = xContent.getName();
                Point point = xContent.getPoint();
                map.put(name, point);
            });
        }
        return map;
    }

    @Override
    public Long lPush(String key, String value) {
        return listOperations.leftPush(key, value);
    }

    @Override
    public Long rPush(String key, String value) {
        return listOperations.rightPush(key, value);
    }

    @Override
    public String lPop(String key) {
        return listOperations.leftPop(key);
    }

    @Override
    public String rPop(String key) {
        return listOperations.rightPop(key);
    }

    @Override
    public Long lSize(String key) {
        return listOperations.size(key);
    }

    @Override
    public String lIndex(String key, Integer index) {
        return listOperations.index(key, index);
    }

    @Override
    public Long lPushX(String key, String value) {
        return listOperations.leftPushIfPresent(key, value);
    }

    @Override
    public Long rPushX(String key, String value) {
        return listOperations.rightPushIfPresent(key, value);
    }

    @Override
    public void lPush(String key, List<String> values) {
        listOperations.leftPushAll(key, values);
    }

    @Override
    public void rPush(String key, List<String> values) {
        listOperations.rightPushAll(key, values);
    }

    @Override
    public Long lRem(String key, String value, Integer count) {
        return listOperations.remove(key, count, value);
    }

    @Override
    public List<String> lRange(String key, Long startIndex, Long endIndex) {
        return listOperations.range(key, startIndex, endIndex);
    }

    @Override
    public void rPopLPush(String key1, String key2) {
        listOperations.rightPopAndLeftPush(key1, key2);
    }

    @Override
    public void lTrim(String key, Long startIndex, Long endIndex) {
        listOperations.trim(key, startIndex, endIndex);
    }

    @Override
    public void pFAdd(String key, String value) {
        hyperLogLogOperations.add(key, value);
    }

    @Override
    public void pFDel(String key) {
        hyperLogLogOperations.delete(key);
    }

    @Override
    public Long pFCount(String key) {
        return hyperLogLogOperations.size(key);
    }

    @Override
    public void pfMerge(String key, List<String> keys) {
        hyperLogLogOperations.union(key, keys.toArray(new String[0]));
    }
}