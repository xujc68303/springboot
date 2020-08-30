package com.util.expiring.service;

import com.google.common.collect.Maps;
import com.util.expiring.api.ExpiringMapService;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName ExpiringMapServiceImpl
 * @Author jiachenXu
 * @Date 2020/8/29 21:53
 * @Description
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class ExpiringMapServiceImpl implements ExpiringMapService {

    private static volatile ExpiringMap<String, Object> expiringMap;

    private static int maxSize = 100;

    private static TimeUnit timeUnit = TimeUnit.SECONDS;

    private static long expiration = 60 * 60 * 24;

    private final ExpirationListener<String, Object> expirationListener = ((theKey, theValue) -> {
        log.info("过期监听事件: key:{}, value:{}", theKey, theValue);
    });

    /**
     * 初始化配置
     *
     * @param maxSize    最大容量
     * @param timeUnit   缓存过期时间单位
     * @param expiration 缓存过期时间
     */
    private static void initConfig() {
        if (expiringMap.isEmpty( )) {
            synchronized (ExpiringMap.class) {
                expiringMap = ExpiringMap.builder( )
                        .maxSize(maxSize)
                        .expiration(expiration, timeUnit)
                        .variableExpiration( )
                        // 设置过期策略为创建或更新值后
                        .expirationPolicy(ExpirationPolicy.CREATED)
                        .build( );
            }
        }
    }

    @Override
    public void modifyConfig(int maxSize, TimeUnit timeUnit, long expiration) {
        if (maxSize != 0) {
            maxSize = maxSize;
        }
        if (timeUnit != null) {
            timeUnit = timeUnit;
        }
        if (expiration != 0) {
            expiration = expiration;
        }
    }

    @Override
    public void addListener() {
        if(!expiringMap.isEmpty()){
            expiringMap.addAsyncExpirationListener(expirationListener);
        }
    }

    @Override
    public void removeListener() {
        if(!expiringMap.isEmpty()){
            expiringMap.removeAsyncExpirationListener(expirationListener);
        }
    }

    @Override
    public boolean isExist(String k) {
        if (!expiringMap.containsKey(k)) {
            return false;
        }
        long expiration = expiringMap.getExpectedExpiration(k);
        return System.currentTimeMillis( ) > expiration;
    }

    @Override
    public boolean put(String k, Object v, TimeUnit timeUnit, long expiration) {
        return expiringMap.put(k, v, expiration, timeUnit) != null;
    }

    @Override
    public boolean putIfAbsent(String k, Object v) {
        return expiringMap.putIfAbsent(k, v) == null;
    }

    @Override
    public boolean putAll(Map<String, Object> map) {
        expiringMap.putAll(map);
        return true;
    }

    @Override
    public boolean del(String k) {
        return expiringMap.remove(k) != null;
    }

    @Override
    public Object get(String k) {
        return expiringMap.get(k);
    }

    @Override
    public boolean update(String k, Object v) {
        return expiringMap.replace(k, v) != null;
    }

    @Override
    public long getSize() {
        return expiringMap.size( );
    }

    @Override
    public List<String> getKeys() {
        List<String> result = new ArrayList<>( );
        expiringMap.entrySet( ).forEach(x -> {
            result.add(x.getKey( ));
        });
        return result;
    }

    @Override
    public Map<String, Object> getAll() {
        Map<String, Object> result = Maps.newLinkedHashMap( );
        expiringMap.entrySet( ).forEach(x -> {
            result.put(x.getKey( ), x.getValue( ));
        });
        return result;
    }

    @Override
    public void clear() {
        expiringMap.clear( );
    }


}
