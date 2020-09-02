package com.xjc.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Version 1.0
 * @ClassName RedissonConfig
 * @Author jiachenXu
 * @Date 2020/9/1 23:07
 * @Description
 */
@Configuration
public class RedissonConfig {

    @Value("${redisson.address}")
    private String address;

    @Bean(name = "redissonClient")
    public RedissonClient initConfig() {
        Config config = new Config( );
        config.useSingleServer( ).setAddress(address);
        return Redisson.create(config);
    }

}
