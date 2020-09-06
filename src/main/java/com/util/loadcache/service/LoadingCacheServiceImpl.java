package com.util.loadcache.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.util.loadcache.api.LoadingCacheService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName LoadingCacheServiceImpl
 * @Author jiachenXu
 * @Date 2020/9/1
 * @Description
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class LoadingCacheServiceImpl extends Thread implements LoadingCacheService {

    /**
     * 缓存容量最大值，超过最大值LRU进行清除
     */
    private volatile static long MAX_SIZE = 10000;

    /**
     * 初始化缓存容量
     */
    private volatile static int INITI_ALCAPA_CITY = 1000;

    /**
     * 缓存过期时间
     */
    private volatile static long DURA_TION = 60 * 60;

    /**
     * 缓存刷新时间
     */
    private volatile static long REFRESH = 60;

    /**
     * 时间单位秒
     */
    private static TimeUnit UNIT = TimeUnit.SECONDS;

    private static final ScheduledExecutorService scheduledexecutorservice = Executors.newSingleThreadScheduledExecutor( );

    private static volatile LoadingCache<String, Object> loadingCache;

    static {
        synchronized (LoadingCache.class) {
            loadingCache = CacheBuilder
                    .newBuilder( )
                    .recordStats( )
                    .maximumSize(MAX_SIZE)
                    .initialCapacity(INITI_ALCAPA_CITY)
                    // 写入60分钟后过期
                    .expireAfterWrite(DURA_TION, UNIT)
                    // 手动刷新
                    .refreshAfterWrite(REFRESH, UNIT)
                    .removalListener((monitor) -> {
                        log.info("key:{}被移除", monitor.getKey( ));
                    })
                    .build(new CacheLoader<String, Object>( ) {

                        @Override
                        public Object load(@NotNull String key) throws Exception {
                            return loadingCache.getUnchecked(key);
                        }
                    });

            asyncRefresh( );
        }
    }

    @Override
    public void run() {
        this.asMap( ).keySet( ).forEach(k -> loadingCache.refresh(k));
    }

    private static void asyncRefresh() {
        try {
            scheduledexecutorservice.scheduleAtFixedRate(new LoadingCacheServiceImpl( ), 0, REFRESH, UNIT);
        } catch (RuntimeException e) {
            scheduledexecutorservice.shutdown( );
        }
    }


    @Override
    public void modifyConfig(long maxSize, long duration, TimeUnit timeUnit, int initialCapacity, long refresh) {
        if (maxSize == 0 || duration == 0 || timeUnit == null || initialCapacity == 0 || refresh == 0) {
            return;
        }
        MAX_SIZE = maxSize;
        DURA_TION = duration;
        UNIT = timeUnit;
        INITI_ALCAPA_CITY = initialCapacity;
        REFRESH = refresh;
    }

    @Override
    public boolean put(String key, Object value) {
        Map<String, Object> result = Maps.newLinkedHashMap( );
        result.put(key, value);
        return this.putAll(result);
    }

    @Override
    public boolean putAll(Map<String, Object> map) {
        loadingCache.putAll(map);
        return true;
    }

    @Override
    public Object get(String key) {
        List<String> keys = Arrays.asList(key);
        return this.getAll(keys);
    }

    @Override
    public Map<String, Object> getAll(List<String> keys) {
        return loadingCache.getAllPresent(keys);
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> result = Maps.newLinkedHashMap( );
        loadingCache.asMap( ).forEach(result::put);
        return result;
    }

    @Override
    public boolean delete(String key) {
        List<String> keys = Arrays.asList(key);
        return this.deleteAll(keys);
    }

    @Override
    public boolean deleteAll(List<String> keys) {
        loadingCache.invalidateAll(keys);
        return true;
    }

    @Override
    public long size() {
        return loadingCache.size( );
    }

    @Override
    public boolean clear() {
        loadingCache.invalidateAll( );
        return true;
    }
}
