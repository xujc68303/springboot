package com.xjc.guava.bloomfilter.api;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName BloomFilterService
 * @Author jiachenXu
 * @Date 2020/9/1 21:35
 * @Description 布隆过滤器
 */
public interface BloomFilterService {

    /**
     * 修改布隆过滤器
     *
     * @param size 最大容量
     * @param fpp  误报率
     * @return 执行结果
     */
    boolean modifyConfig(long size, double fpp);

    /**
     * 添加过滤数据
     * @param pattern
     * @return
     */
    boolean put(String pattern);

    /**
     * 添加过滤数据
     * @param patterns
     * @return
     */
    boolean put(List<String> patterns);

    /**
     * 过滤
     * @param patch 匹配数据
     * @return
     */
    boolean match(String patch);

}
