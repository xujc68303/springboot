package com.xjc.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Version 1.0
 * @ClassName RedissonConfig
 * @Author jiachenXu
 * @Date 2020/9/1
 * @Description
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.sentinel.master}")
    private String masterName;

    @Value("${redisson.address}")
    private String address;

    @Value("${spring.redis.sentinel.nodes}")
    private String nodes;

//    @Bean(name = "redissonClient")
//    public RedissonClient initConfig() {
//        Config config = new Config( );
//        config.useSingleServer( ).setAddress(address);
//        return Redisson.create(config);
//    }

    @Bean(name = "redissonClient")
    public RedissonClient sentinel() {
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName(masterName)
                .addSentinelAddress(nodes.split(","))
                .setReadMode(ReadMode.SLAVE);
        return Redisson.create(config);
    }

}
