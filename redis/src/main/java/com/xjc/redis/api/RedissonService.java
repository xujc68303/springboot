package com.xjc.redis.api;

import org.redisson.api.RRateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName RedissonService
 * @Author jiachenXu
 * @Date 2020/9/1 23:06
 * @Description
 */
public interface RedissonService {

    /**
     * 限流器
     * @param serviceName 名称
     * @param maxFlow 时段最大流量
     * @param section 时间区段
     * @param unit 时间单位
     * @return
     */
    RRateLimiter currentLimiting(String serviceName, long maxFlow, long section, TimeUnit unit);

}
