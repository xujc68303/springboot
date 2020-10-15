package com.xjc.util.reflect.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xjc.util.reflect.api.ClassReflectService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Version 1.0
 * @ClassName ClassReflectServiceImpl
 * @Author jiachenXu
 * @Date 2020/9/27
 * @Description
 */
@Service
@SuppressWarnings("all")
public class ClassReflectServiceImpl implements ClassReflectService {

    @Override
    public Multimap<Object, Map<Object, Object>> getClazzStructureFields(Object clazz, boolean accessible) {
        Multimap<Object, Map<Object, Object>> result = HashMultimap.create( );
        Map<Object, Object> superMap = getSuperFields(clazz, accessible);
        Map<Object, Object> subMap = getClazzFields(clazz, accessible);
        result.putAll("super", Collections.singleton(superMap));
        result.putAll("sub", Collections.singleton(subMap));
        return result;
    }

    @Override
    public Map<Object, Object> getClazzFields(Object clazz, boolean accessible) {
        return doGetFieldResult(doGetClassFields(clazz), clazz, accessible);
    }

    @Override
    public Map<Object, Object> getSuperFields(Object clazz, boolean accessible) {
        return doGetFieldResult(doGetSuperFields(clazz), clazz, accessible);
    }

    private Field[] doGetSuperFields(Object clazz) {
        return clazz.getClass( ).getSuperclass( ).getDeclaredFields( );
    }

    private Field[] doGetClassFields(Object clazz) {
        return clazz.getClass( ).getDeclaredFields( );
    }

    private Map<Object, Object> doGetFieldResult(Field[] fields, Object clazz, boolean accessible) {
        Map<Object, Object> result = new HashMap<>(fields.length);
        Arrays.stream(fields).forEach(f -> {
            try {
                if (accessible) {
                    f.setAccessible(true);
                    result.put(f.getName( ), f.get(clazz));
                } else {
                    int modifiers = f.getModifiers( );
                    if (modifiers == 1 || modifiers == 4) {
                        result.put(f.getName( ), null);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace( );
            }
        });
        return result;
    }
}
