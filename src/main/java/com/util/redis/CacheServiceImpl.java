package com.util.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
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
public class CacheServiceImpl implements CacheService {

    private static final String NOT_EXIST = "NX";

    private static final String EXIST = "XX";

    private static final Long ONE_RESULT = 1L;

    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) else return 0 end ";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
            return stringRedisTemplate.opsForValue( ).get(key);
        } catch (RuntimeException e) {
            log.error("cache-get error, key=" + key);
        }
        return null;
    }

    @Override
    public Boolean setWithExpire(String key, Object value, long expire, TimeUnit unit) {
        try {
            String v = JSON.toJSONString(value);
            return stringRedisTemplate.opsForValue( ).setIfAbsent(key, v, expire, unit);
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
                    return stringRedisTemplate.opsForValue( ).setIfAbsent(key, v, expire, unit);
                } else if (EXIST.equalsIgnoreCase(nxxx)) {
                    return stringRedisTemplate.opsForValue( ).setIfPresent(key, v, expire, unit);
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
                return stringRedisTemplate.opsForValue( ).setIfAbsent(key, v, lockWaitTimeOut, unit);
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
        stringRedisTemplate.opsForValue( ).setBit(key, offset, value);
    }

    @Override
    public Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes( )));
    }

    @Override
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue( ).increment(key, delta);
    }

    @Override
    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue( ).decrement(key, delta);
    }

    @Override
    public void zsetAdd(String key, String value, long delta) {
        stringRedisTemplate.opsForZSet( ).add(key, value, delta);
    }

    @Override
    public Boolean zsetDel(String key, String value) {
        return stringRedisTemplate.opsForZSet( ).remove(key, value) == 1;
    }

    @Override
    public Set<String> zsetRever(String key, long start, long end) {
        return stringRedisTemplate.opsForZSet( ).reverseRange(key, start, end);
    }

    @Override
    public Integer zetCount(String key) {
        return zsetRever(key, 0, -1).size( );
    }

    @Override
    public Long reverseRank(String key, String value) {
        return stringRedisTemplate.opsForZSet( ).reverseRank(key, value);
    }

}