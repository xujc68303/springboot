package com.xjc.mysql.mapper.config;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.encrypt.api.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.EncryptorRuleConfiguration;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Description 数据脱敏
 * @Date 2021/7/23 17:29
 * @Created by Xujc
 */
public class EncryptRuleConfig {

    @Value("${spring.shardingsphere.encrypt.encryptors.encrytor_aes.type}")
    private String encryption;
    @Value("${spring.shardingsphere.encrypt.encryptors.encrytor_aes.key}")
    private String keys;

    public EncryptRuleConfiguration getEncryptRuleConfiguration(String key, Map<String, String> columnMap, String table) {
        EncryptRuleConfiguration encryptRuleConfig = new EncryptRuleConfiguration();
        Properties props = new Properties();
        if (StringUtils.isBlank(key)) {
            key = keys;
        }
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

    public static void main(String[] args) {
        EncryptRuleConfig ruleConfig = new EncryptRuleConfig();
        Map<String, String> columnMap = Maps.newHashMap();
        columnMap.put("address", "");
        columnMap.put("password", "");
        ruleConfig.getEncryptRuleConfiguration("", columnMap, "t_customer");
    }


}
