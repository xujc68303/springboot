package com.xjc.aop.log.config;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "log")
@PropertySource(value = "classpath:field-mapping.yml", factory = YamlPropertySourceFactory.class)
public class FieldMappingConfig {

    private Map<String, Map<String, String>> fieldDisplay = new HashMap<>();

    public Map<String, Map<String, String>> getFieldDisplay() {
        return fieldDisplay;
    }

    public void setFieldDisplay(Map<String, Map<String, String>> fieldDisplay) {
        this.fieldDisplay = fieldDisplay;
    }

    public String getDisplayField(String objectType, String fieldName) {
        if (fieldDisplay != null && fieldDisplay.containsKey(objectType)) {
            return fieldDisplay.get(objectType).getOrDefault(fieldName, fieldName);
        }
        return fieldName;
    }

    public List<String> getMonitoredFields(String objectType) {
        if (fieldDisplay != null && fieldDisplay.containsKey(objectType)) {
            Map<String, String> fieldMap = fieldDisplay.get(objectType);
            return fieldMap != null ? new ArrayList<>(fieldMap.keySet()) : Lists.newArrayList();
        }
        return Lists.newArrayList();
    }
}