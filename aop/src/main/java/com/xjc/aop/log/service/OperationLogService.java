package com.xjc.aop.log.service;

import com.xjc.aop.log.config.FieldMappingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Service
public class OperationLogService {

    @Autowired
    private FieldMappingConfig fieldMappingConfig;

    /**
     * 记录字段变更日志（通用入口）
     *
     * @param operationObject 模块标识，如 "delivery_point"
     * @param objectId        对象ID（字符串）
     * @param objectName      对象名称（用于日志展示）
     * @param oldValues       旧值 Map（key 为字段名）
     * @param newValues       新值 Map（key 为字段名）
     */
    public void logFieldChanges(String operationObject, String objectId, String objectName,
                                Map<String, Object> oldValues, Map<String, Object> newValues) {
        if (objectId == null || oldValues == null || newValues == null) {
            return;
        }
        List<String> fields = fieldMappingConfig.getMonitoredFields(operationObject);
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (String field : fields) {
            Object oldValue = oldValues.get(field);
            Object newValue = newValues.get(field);
            if (!Objects.equals(oldValue, newValue)) {
                String displayField = fieldMappingConfig.getDisplayField(operationObject, field);
                saveLog("UPDATE",
                        getCurrentUser(),
                        operationObject,
                        objectId,
                        objectName,
                        displayField,
                        safeToString(oldValue),
                        safeToString(newValue));
            }
        }
    }

    /**
     * 从任意对象中提取配置中指定的监控字段，转为 Map
     */
    public Map<String, Object> extractMonitoredFields(Object obj, String operationObject) {
        if (obj == null) return Collections.emptyMap();
        List<String> fields = fieldMappingConfig.getMonitoredFields(operationObject);
        if (fields == null || fields.isEmpty()) return Collections.emptyMap();
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (String field : fields) {
            try {
                Field f = clazz.getDeclaredField(field);
                f.setAccessible(true);
                map.put(field, f.get(obj));
            } catch (Exception e) {
            }
        }
        return map;
    }

    private String safeToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private String getCurrentUser() {
        // TODO: 替换为真实用户，例如：
        // return SecurityContextHolder.getContext().getAuthentication().getName();
        return "admin";
    }

    private void saveLog(String operationType, String operator, String operationObject,
                         String objectId, String objectName, String changedField,
                         String oldValue, String newValue) {
        try {
//            OperationLog log = new OperationLog();
//            log.setOperationType(operationType);
//            log.setOperator(operator);
//            log.setOperationTime(LocalDateTime.now());
//            log.setOperationObject(operationObject);
//            log.setObjectId(objectId);
//            log.setObjectName(objectName);
//            log.setChangedField(changedField);
//            log.setOldValue(oldValue);
//            log.setNewValue(newValue);
//            logRepository.save(log);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
}
