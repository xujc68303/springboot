package com.xjc.redis.service;

import com.xjc.redis.api.RedissonService;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName RedissonServiceImpl
 * @Author jiachenXu
 * @Date 2020/9/1 23:06
 * @Description
 */
@Service
public class RedissonServiceImpl implements RedissonService {

    @Autowired
    private RedissonClient redissonClient;

    private volatile RRateLimiter rRateLimiter;

    @Override
    public RRateLimiter currentLimiting(String serviceName, long maxSize, long section, TimeUnit unit) {
        synchronized (RRateLimiter.class) {
            rRateLimiter(serviceName);
            RateIntervalUnit intervalUnit = RateIntervalUnit.valueOf(unit.name( ));
            if (rRateLimiter.trySetRate(RateType.PER_CLIENT, maxSize, section, intervalUnit)) {
                return rRateLimiter;
            }
            return null;
        }
    }

    private void rRateLimiter(String serviceName) {
        this.rRateLimiter = redissonClient.getRateLimiter(serviceName);
    }
}
