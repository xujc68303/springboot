package com.util.utils.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.Objects;

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

    private static final String CACHE_UTIL_KEY = "cacheUtil";

    private static final String NOT_EXIST = "NX";

    private static final String SECONDS = "EX";

    private static final String MILLISECONDS = "PX";

    private static final String OK = "OK";

    private static final String ONE_RESULT = "1";

    @Autowired
    private static volatile JedisPool jedisPool;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private static StopWatch stopWatch;

    /**
     * 初始化连接
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
        return jedisPool.getResource();
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

    /**
     * 计数器初始化
     * @param functionName 函数
     * @return StopWatch
     */
    private static StopWatch getWatch(String functionName) {
        stopWatch = new StopWatch(functionName);
        stopWatch.start( );
        return stopWatch;
    }

    private static void stopWatch() {
        stopWatch.stop( );
    }


    @Override
    public Boolean exists(String key) {
        StopWatch stopWatch = getWatch("cache-exists");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            return jedis.exists(key);
        } catch (RuntimeException e) {
            log.error("cache-get error," + stopWatch.getTotalTimeMillis( ), ",key=" + key);
            return false;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }
    }

    @Override
    public Object get(String key) {
        StopWatch stopWatch = getWatch("cache-get");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            return jedis.get(key);
        } catch (RuntimeException e) {
            log.error("cache-get error" + stopWatch.getTotalTimeMillis( ), "key=" + key);
            return null;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }
    }

    @Override
    public String getString(String key) {
        StopWatch stopWatch = getWatch("cache-getString");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            return jedis.get(key);
        } catch (RuntimeException e) {
            log.error("cache-getString error" + stopWatch.getTotalTimeMillis( ), "key=" + key);
            return null;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }
    }

    @Override
    public Boolean setWithExpire(String key, Object value, int expire) {
        StopWatch stopWatch = getWatch("cache-setexWithExpire");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            String v = JSON.toJSONString(value);
            String result = jedis.setex(key, expire, v);
            if (!OK.equals(result)) {
                log.error("cache-setexWithExpire failed" + stopWatch.getTotalTimeMillis( ) + "key=" + key);
                return false;
            }
            log.info("cache-setexWithExpire success" + stopWatch.getTotalTimeMillis( ) + "key=" + key);
            return true;
        } catch (
                RuntimeException e) {
            log.error("cache-setexWithExpire error", e);
            return false;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }

    }


    @Override
    public Object getKeyWithExpire(String key, int expire) {
        StopWatch stopWatch = getWatch("cache-getKeyWithExpire");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            Object value = jedis.get(key);
            if (Objects.nonNull(value)) {
                if (1 == jedis.expire(key, expire)) {
                    log.info("cache-getKeyWithExpire success" + stopWatch.getTotalTimeMillis( ) + "key=" + key);
                    return value;
                }
            }
            log.error("cache-getKeyWithExpire failed" + stopWatch.getTotalTimeMillis( ) + "key=" + key);
            return null;
        } catch (RuntimeException e) {
            log.error("cache-getKeyWithExpire error", e);
            return null;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }
    }

    @Override
    public Boolean removeObject(String key) {
        StopWatch stopWatch = getWatch("cache-removeObject");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            if (jedis.exists(key)) {
                if (jedis.del(key) > 0) {
                    log.info("cache-removeObject success" + stopWatch.getTotalTimeMillis( ) + "key=" + key);
                    return true;
                }
            }
            log.error("cache-removeObject failed" + stopWatch.getTotalTimeMillis( ) + "key=" + key);
            return false;
        } catch (RuntimeException e) {
            log.error("cache-removeObject error", e);
            return false;
        }
    }

    @Override
    public Boolean renameByKey(String oldKey, String newKey) {
        StopWatch stopWatch = getWatch("cache-renameByKey");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            if (jedis.exists(oldKey)) {
                if (OK.equals(jedis.rename(oldKey, newKey))) {
                    log.info("cache-removeObject success" + stopWatch.getTotalTimeMillis( ) +
                            "oldKey=" + oldKey + "newKey=" + newKey);
                    return true;
                }
            }
            return false;
        } catch (RuntimeException e) {
            log.error("cache-renameByKey error", e);
            return false;
        }
    }

    @Override
    public boolean distributedLock(String key, Object value) {
        StopWatch stopWatch = getWatch("cache-distributedLock");
        try {
            final String v = JSON.toJSONString(value);
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection( );
                return String.valueOf(commands.setnx(CACHE_UTIL_KEY + key, v) > 0);
            };
            Object result = redisTemplate.execute(callback);
            if (!StringUtils.isEmpty(result)) {
                log.error("cache-distributedLock failed" + stopWatch.getLastTaskTimeMillis( ) + "key=" + key);
                return false;
            }
            log.info("cache-distributedLock success" + stopWatch.getLastTaskTimeMillis( ) + "key=" + key);
            return true;

        } catch (Exception e) {
            log.error("cache-lockToTransactional error" + stopWatch.getLastTaskTimeMillis( ), e);
            return false;
        } finally {
            stopWatch.stop( );
        }
    }

    @Override
    public boolean distributedLock(String key, Object value, String expx, Long expire) {
        StopWatch stopWatch = getWatch("cache-distributedLock");
        Jedis jedis = null;
        try {
            jedis = initJedis( );
            if (StringUtils.isEmpty(expx)) {
                expx = SECONDS;
            }
            final String v = JSON.toJSONString(value);

            String result = jedis.set(CACHE_UTIL_KEY + key, v, NOT_EXIST, expx, expire);
            if (!OK.equals(result)) {
                log.error("cache-distributedLock failed" + stopWatch.getLastTaskTimeMillis( ) + "key=" + key);
                return false;
            }
            log.info("cache-distributedLock success" + stopWatch.getLastTaskTimeMillis( ) + "key=" + key);
            return true;

        } catch (Exception e) {
            log.error("cache-lock error" + stopWatch.getLastTaskTimeMillis( ), e);
            return false;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }
    }

    @Override
    public boolean preemptiveLock(String key, Object value, String expx, String expire, Long lockWaitTimeOut) {
        Jedis jedis = null;
        try {
            jedis = initJedis( );

            long deadTimeLine = System.currentTimeMillis( ) + lockWaitTimeOut;

            for (; ; ) {
                // 循环退出剩余时间，秒杀结束
                if (deadTimeLine - lockWaitTimeOut <= 0L) {
                    return false;
                }

                if (StringUtils.isEmpty(expx)) {
                    expx = SECONDS;
                }
                final String v = JSON.toJSONString(value);
                String result = jedis.set(key, v, NOT_EXIST, expx, lockWaitTimeOut);
                if (!OK.equals(result)) {
                    log.error("cache-preemptiveLock failed", key);
                    return false;
                }
                log.info("cache-preemptiveLock success", key);
                return true;
            }

        } catch (Exception e) {
            log.error("cache-preemptiveLock error", e);
            return false;
        } finally {
            closeJedisPool(jedis);
        }
    }

    @Override
    public Boolean unlock(String key, Object value) {
        StopWatch stopWatch = getWatch("cache-unlock");
        Jedis jedis = null;
        try {
            jedis = initJedis( );

            // 参数KEYS[1]赋值为key，ARGV赋值为value
            final String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return " +
                    "redis.call('del', KEYS[1]) else return 0 end";
            final String v = JSON.toJSONString(value);

            Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(v));
            if (!ONE_RESULT.equals(result)) {
                log.error("cache-unlock error", +stopWatch.getLastTaskTimeMillis( ) + "key=" + key);
                return false;
            }
            log.info("cache-unlock success", +stopWatch.getLastTaskTimeMillis( ) + "key=" + key);
            return true;

        } catch (Exception e) {
            log.error("cache-unlock error" + stopWatch.getLastTaskTimeMillis( ), e);
            return false;
        } finally {
            closeJedisPool(jedis);
            stopWatch( );
        }
    }

}