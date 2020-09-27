package com.util.reflect.api;

import com.google.common.collect.Multimap;

import java.util.Map;

/**
 * @Version 1.0
 * @ClassName ClassReflectService
 * @Author jiachenXu
 * @Date 2020/9/27
 * @Description
 */
public interface ClassReflectService {

    /**
     * 获取本类、父类 字段属性
     * @param clazz
     * @param accessible
     * @return
     */
    Multimap<Object, Map<Object, Object>> getClazzStructureFields(Object clazz, boolean accessible);

    /**
     * 获取本类字段属性
     * @param clazz
     * @param accessible
     * @return
     */
    Map<Object, Object> getClazzFields(Object clazz, boolean accessible);

    /**
     * 获取父类字段属性
     * @param clazz
     * @param accessible
     * @return
     */
    Map<Object, Object> getSuperFields(Object clazz, boolean accessible);
}
