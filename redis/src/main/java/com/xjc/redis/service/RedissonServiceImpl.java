package com.xjc.redis.service;

import com.google.common.collect.Maps;
import com.xjc.redis.api.RedissonService;
import com.xjc.redis.config.GeoRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName RedissonServiceImpl
 * @Author jiachenXu
 * @Date 2020/9/1
 * @Description
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class RedissonServiceImpl implements RedissonService {

    @Autowired
    private RedissonClient redissonClient;

    private volatile RRateLimiter rRateLimiter;

    private volatile RBloomFilter<Object> bloomFilter;

    private static volatile boolean INIT_FLAG = false;

    private volatile RLock rLock;

    private volatile RLock fairLock;

    private volatile RSemaphore rSemaphore;

    private volatile RCountDownLatch rCountDownLatch;

    private volatile RBuckets rBuckets;

    private volatile RGeo<Object> rGeo;

    private volatile RBitSet rBitSet;

    @Override
    public RRateLimiter currentLimiting(String serviceName, long maxSize, long section, long expired, TimeUnit unit) {
        getRateLimiter(serviceName);
        synchronized (RRateLimiter.class) {
            RateIntervalUnit intervalUnit = RateIntervalUnit.valueOf(unit.name());
            if (rRateLimiter.trySetRate(RateType.PER_CLIENT, maxSize, section, intervalUnit)) {
                if (expired != 0 && unit != null) {
                    if (!rRateLimiter.expire(expired, unit)) {
                        return null;
                    }
                }
                return rRateLimiter;
            }
        }
        rRateLimiter.delete();
        return null;
    }

    @Override
    public RateLimiterConfig getLimiterConfig(String serviceName) {
        getRateLimiter(serviceName);
        return rRateLimiter.getConfig();
    }

    @Override
    public boolean clearLimiting(String serviceName) {
        getRateLimiter(serviceName);
        return rRateLimiter.clearExpire();
    }

    @Override
    public boolean initBloomFilter(long maxSize, double fpp) {
        synchronized (RBloomFilter.class) {
            if (!INIT_FLAG) {
                getBloomFilter();
                if (bloomFilter.tryInit(maxSize, fpp)) {
                    INIT_FLAG = true;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean addBloomFilter(String pattern) {
        return addBloomFilter(Arrays.asList(pattern));
    }

    @Override
    public boolean addBloomFilter(List<String> patterns) {
        synchronized (RBloomFilter.class) {
            if (INIT_FLAG) {
                getBloomFilter();
                patterns.forEach(x -> bloomFilter.add(x));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean matchBloomFilter(String pattern) {
        getBloomFilter();
        return bloomFilter.contains(pattern);
    }

    @Override
    public boolean clearBloomFilter() {
        synchronized (RBloomFilter.class) {
            if (INIT_FLAG) {
                getBloomFilter();
                if (bloomFilter.delete()) {
                    INIT_FLAG = false;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public RLock reentrantLock(String lockName, long wait, long expired, TimeUnit timeUnit) {
        getLock(lockName);
        try {
            if (!rLock.isLocked()) {
                boolean result = rLock.tryLock(wait, expired, timeUnit);
                if (result) {
                    return rLock;
                }
            }
        } catch (Exception e) {
            log.error("redisson reentrantLock error={}, thread id={}", e, Thread.currentThread().getId());
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        return null;
    }

    @Override
    public RLock fairLock(String lockName, long wait, long expired, TimeUnit timeUnit) {
        getFairLock(lockName);
        if (!fairLock.isLocked()) {
            try {
                boolean result = fairLock.tryLock(wait, expired, timeUnit);
                if (result) {
                    return fairLock;
                }
            } catch (Exception e) {
                log.error("redisson fairLock error={}, thread id={}", e, Thread.currentThread().getId());
                if (fairLock.isLocked() && fairLock.isHeldByCurrentThread()) {
                    fairLock.unlock();
                }
            }
        }
        return null;
    }

    @Override
    public RedissonMultiLock multiLock(long wait, long expired, TimeUnit timeUnit, String... lockName) {
        if (lockName.length == 3) {
            RLock rLock1 = getLock(lockName[0]);
            RLock rLock2 = getLock(lockName[1]);
            RLock rLock3 = getLock(lockName[2]);
            RedissonMultiLock redissonMultiLock = new RedissonMultiLock(rLock1, rLock2, rLock3);
            try {
                if (!redissonMultiLock.isLocked()) {
                    boolean result = redissonMultiLock.tryLock(wait, expired, timeUnit);
                    if (result) {
                        return redissonMultiLock;
                    }
                }
            } catch (Exception e) {
                log.error("redisson multiLock error={}, thread id={}", e, Thread.currentThread().getId());
                if (redissonMultiLock.isLocked() && redissonMultiLock.isHeldByCurrentThread()) {
                    redissonMultiLock.unlock();
                }
            }
        }
        return null;
    }

    @Override
    public RedissonRedLock readLock(long wait, long expired, TimeUnit timeUnit, String... lockName) {
        if (lockName.length == 3) {
            RLock rLock1 = getLock(lockName[0]);
            RLock rLock2 = getLock(lockName[1]);
            RLock rLock3 = getLock(lockName[2]);
            RedissonRedLock redissonRedLock = new RedissonRedLock(rLock1, rLock2, rLock3);
            try {
                if (!redissonRedLock.isLocked()) {
                    boolean result = redissonRedLock.tryLock(wait, expired, timeUnit);
                    if (result) {
                        return redissonRedLock;
                    }
                }
            } catch (Exception e) {
                log.error("redisson multiLock error={}, thread id={}", e, Thread.currentThread().getId());
                if (redissonRedLock.isLocked() && redissonRedLock.isHeldByCurrentThread()) {
                    redissonRedLock.unlock();
                }
            }
        }
        return null;
    }

    @Override
    public RSemaphore semaphore(String serviceName, int permits, long wait, long expired, TimeUnit timeUnit) {
        rSemaphore = getSemaphore(serviceName);
        if (rSemaphore == null) {
            try {
                if (rSemaphore.tryAcquire(permits, wait, timeUnit)) {
                    if (expired != 0 && timeUnit != null) {
                        if (!rSemaphore.expire(expired, timeUnit)) {
                            return null;
                        }
                    }
                    return rSemaphore;
                }
            } catch (Exception e) {
                log.error("redisson semaphore error={}, thread id={}", e, Thread.currentThread().getId());
                deleteSemaphore(serviceName);
            }
        }
        return null;
    }

    @Override
    public boolean deleteSemaphore(String serviceName) {
        getSemaphore(serviceName);
        return rSemaphore.delete();
    }

    @Override
    public RCountDownLatch countDownLatch(String serviceName, int count) {
        rCountDownLatch = getrCountDownLatch(serviceName);
        if (rCountDownLatch.isExists()) {
            rCountDownLatch.delete();
            rCountDownLatch = getrCountDownLatch(serviceName);

            if (rCountDownLatch.trySetCount(count)) {
                return rCountDownLatch;
            }
        }
        return null;
    }

    @Override
    public boolean setBucket(Map<String, Object> map) {
        if (rBuckets == null) {
            rBuckets = redissonClient.getBuckets();
        }
        return rBuckets.trySet(map);
    }

    @Override
    public Map<String, Object> getBuckets(List<String> keys) {
        getrBuckets();
        return rBuckets.get(keys.toArray(new String[0]));
    }

    @Override
    public long deleteBuckets(List<String> keys) {
        getrBuckets();
        return rBuckets.delete(keys.toArray(new String[0]));
    }

    @Override
    public boolean addressAdd(List<GeoRequest> geoRequests) {
        getGeo();
        geoRequests.forEach(e -> rGeo.add(e.getLongitude(), e.getLatitude(), e.getMember()));
        return true;
    }

    @Override
    public Map<Object, GeoPosition> addressGet(List<String> members) {
        getGeo();
        Map<Object, GeoPosition> result = Maps.newLinkedHashMap();
        members.forEach(e -> {
            Map<Object, GeoPosition> map = rGeo.pos(e);
            map.forEach(result::put);
        });
        return result;
    }

    @Override
    public void bitSetAndModify(long key, boolean value) {
        getBit();
        rBitSet.set(key, value);
    }

    @Override
    public boolean bitGet(long key) {
        getBit();
        return rBitSet.get(key);
    }

    @Override
    public boolean bitRemove(List<Long> keys) {
        getBit();
        keys.forEach(k -> rBitSet.clear(k, k));
        return true;
    }

    private void getRateLimiter(String serviceName) {
        if (bloomFilter != null) {
            this.rRateLimiter = redissonClient.getRateLimiter(serviceName);
        }
    }

    private void getBloomFilter() {
        if (bloomFilter != null) {
            bloomFilter = redissonClient.getBloomFilter("BloomFilter");
        }
    }

    private RLock getLock(String lockName) {
        rLock = redissonClient.getLock(lockName);
        return rLock;
    }

    private void getFairLock(String lockName) {
        fairLock = redissonClient.getFairLock(lockName);
    }

    private RSemaphore getSemaphore(String serviceName) {
        rSemaphore = redissonClient.getSemaphore(serviceName);
        return rSemaphore;
    }

    private RCountDownLatch getrCountDownLatch(String serviceName) {
        rCountDownLatch = redissonClient.getCountDownLatch(serviceName);
        return rCountDownLatch;
    }

    private RBuckets getrBuckets() {
        rBuckets = redissonClient.getBuckets();
        return rBuckets;
    }

    private void getGeo() {
        if (rGeo == null) {
            rGeo = redissonClient.getGeo("address");
        }
    }

    private void getBit() {
        if (rBitSet == null) {
            rBitSet = redissonClient.getBitSet("bit");
        }
    }

}
