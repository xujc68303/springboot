package com.xjc.guava.datastructure;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

import java.util.Map;
import java.util.Set;

/**
 * @Version 1.0
 * @ClassName Maps
 * @Author jiachenXu
 * @Date 2020/9/27 21:06
 * @Description
 */
public class Maps {

    Multimap<Object, Object> arrayListMultimap = ArrayListMultimap.create( );

    Multimap<Object, Map<Object, Object>> objectMapMultimap = HashMultimap.create( );

    SetMultimap<Object, Set<Object>> setMultimap = HashMultimap.create( );

    public static void main(String[] args) {
        SetMultimap<Object, Object> setMultimap = HashMultimap.create( );
        setMultimap.put("test1", "1");
        setMultimap.put("test1", "2");
        setMultimap.put("test1", "3");
        setMultimap.put("test1", "4");
        setMultimap.put("test1", "5");
        System.out.println(setMultimap);
    }
}
