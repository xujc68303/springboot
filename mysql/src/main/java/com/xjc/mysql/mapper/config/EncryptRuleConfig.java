package com.xjc.mysql.mapper.config;

import org.apache.shardingsphere.encrypt.api.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.EncryptorRuleConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Description EncryptRuleConfig
 * @Date 2021/7/23 17:29
 * @Created by Xujc
 */
@Configuration
public class EncryptRuleConfig {

    @Value("${spring.shardingsphere.encrypt.encryptors.encrytor_aes.type}")
    private String encryption;

    public EncryptRuleConfiguration getEncryptRuleConfiguration(String key, Map<String, String> columnMap, String table) {
        EncryptRuleConfiguration encryptRuleConfig = new EncryptRuleConfiguration();

        Properties props = new Properties();
        props.setProperty("spring.shardingsphere.encrypt.encryptors.encrytor_aes.key", key);
        EncryptorRuleConfiguration encryptorRuleConfiguration = new EncryptorRuleConfiguration(encryption, props);

        encryptRuleConfig.getEncryptors().put("aes", encryptorRuleConfiguration);
        Map<String, EncryptColumnRuleConfiguration> columnConfigMaps = new HashMap<>(columnMap.size());
        columnMap.forEach((k, v) -> {
            columnConfigMaps.put(k, new EncryptColumnRuleConfiguration(v, k, v, encryption));
        });

        encryptRuleConfig.getTables().put(table, new EncryptTableRuleConfiguration(columnConfigMaps));
        return encryptRuleConfig;
    }


}
