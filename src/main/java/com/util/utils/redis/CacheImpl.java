package com.util.utils.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName CacheImpl
 * @Author jiachenXu
 * @Date 2020/3/5 13:41
 * @Description 缓存工具类
 */
@Slf4j
@Service
public class CacheImpl implements CacheUtil {

    private static final String NOT_EXIST = "NX";

    private static final String SECONDS = "EX";

    private static final String MILLISECONDS = "PX";

    private static final String OK = "OK";

    private static final Long ONE_RESULT = 1L;

    private static final String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "return redis.call('del', KEYS[1]) else return 0 end ";

    @Autowired
    private static volatile JedisPool jedisPool;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 初始化连接
     *
     * @return
     */
    static Jedis initJedis() {
        if (null == jedisPool) {
            synchronized (CacheImpl.class) {
                if (null == jedisPool) {
                    JedisPoolConfig poolConfig = new JedisPoolConfig( );
                    poolConfig.setMaxTotal(1000);
                    poolConfig.setMaxIdle(32);
                    poolConfig.setMaxWaitMillis(1000 * 100);
                    poolConfig.setTestOnBorrow(true);
                    jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);
                }
            }
        }
        return jedisPool.getResource( );
    }

    private void closeJedisPool(Jedis jedis) {
        try {
            if (null != jedis) {
                jedis.close( );
            }
        } catch (RuntimeException e) {
            log.error("cache-returnToPool error", e);
        }
    }


    @Override
    public Boolean exists(String key) {

        Jedis jedis = null;
        try {
            jedis = initJedis( );
            return jedis.exists(key);
        } catch (RuntimeException e) {
            log.error("cache-get error, key=" + key);
            return false;
        } finally {
            closeJedisPool(jedis);
        }
    }

    @Override
    public Object get(String key) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            return jedis.get(key);
        } catch (RuntimeException e) {
            log.error("cache-get error, key=" + key);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    @Override
    public String getString(String key) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            return jedis.get(key);
        } catch (RuntimeException e) {
            log.error("cache-getString error, key=" + key);
        } finally {
            closeJedisPool(jedis);
        }
        return null;
    }

    @Override
    public Boolean setWithExpire(String key, Object value, int expire) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            String v = JSON.toJSONString(value);
            String result = jedis.setex(key, expire, v);
            if (OK.equals(result)) {
                log.info("cache-setexWithExpire success, key=" + key);
                return true;
            }
        } catch (RuntimeException e) {
            log.error("cache-setexWithExpire error", e);
        } finally {
            closeJedisPool(jedis);
        }
        log.error("cache-setexWithExpire failed, key=" + key);
        return false;

    }


    @Override
    public Object getKeyWithExpire(String key, int expire) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            Object value = jedis.get(key);
            if (Objects.nonNull(value)) {
                if (1 == jedis.expire(key, expire)) {
                    log.info("cache-getKeyWithExpire success, key=" + key);
                    return value;
                }
            }
        } catch (RuntimeException e) {
            log.error("cache-getKeyWithExpire error, key=" + key, e);
            return null;
        } finally {
            closeJedisPool(jedis);
        }
        log.error("cache-getKeyWithExpire failed, key=" + key);
        return null;
    }

    @Override
    public Boolean removeObject(String key) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            if (jedis.exists(key)) {
                if (jedis.del(key) > 0) {
                    log.info("cache-removeObject success, key=" + key);
                    return true;
                }
            }
        } catch (RuntimeException e) {
            log.error("cache-removeObject error", e);
        }
        log.error("cache-removeObject failed, key=" + key);
        return false;
    }

    @Override
    public Boolean renameByKey(String oldKey, String newKey) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            if (jedis.exists(oldKey)) {
                if (OK.equals(jedis.rename(oldKey, newKey))) {
                    log.info("cache-removeObject success, oldKey=" + oldKey + "newKey=" + newKey);
                    return true;
                }
            }
            return false;
        } catch (RuntimeException e) {
            log.error("cache-renameByKey error, oldKey=" + oldKey + "newKey=" + newKey, e);
        }
        log.error("cache-distributedLock failed, oldKey=" + oldKey + "newKey=" + newKey);
        return false;
    }

    @Override
    public boolean distributedLock(String key, Object value, String expx, int expire) {
        try {
            final String v = JSON.toJSONString(value);

            // SET_IF_ABSENT--->NX
            // SET_IF_PRESENT--->XX
            if (!StringUtils.isEmpty(expx)) {
                if (SECONDS.equalsIgnoreCase(expx)) {
                    return stringRedisTemplate.opsForValue( ).setIfAbsent(key, v, expire, TimeUnit.SECONDS);
                } else {
                    return stringRedisTemplate.opsForValue( ).setIfAbsent(key, v, expire, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            log.error("cache-lock error", e);
        }

        log.error("cache-distributedLock failed, key=" + key);
        return false;
    }

    @Override
    public boolean preemptiveLock(String key, Object value, int lockWaitTimeOut) {
        try {
            long deadTimeLine = System.currentTimeMillis( ) + lockWaitTimeOut;

            for (; ; ) {
                // 循环退出剩余时间，秒杀结束
                if (deadTimeLine - lockWaitTimeOut <= 0L) {
                    return false;
                }
                final String v = JSON.toJSONString(value);
                return stringRedisTemplate.opsForValue( ).setIfAbsent(key, v, lockWaitTimeOut, TimeUnit.MINUTES);
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

}