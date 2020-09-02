package com.xjc.guava.bloomfilter.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.xjc.guava.bloomfilter.api.BloomFilterService;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName BloomFilterServiceImpl
 * @Author jiachenXu
 * @Date 2020/9/1 21:35
 * @Description
 */
@Service
@SuppressWarnings("all")
public class BloomFilterServiceImpl implements BloomFilterService {

    private static BloomFilter<CharSequence> bloomFilter;

    private static long filterSize = 10000L;

    private static double filterFpp = 0.001;

    private static final String UTF8 = "UTF-8";

    static {
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName(UTF8)), filterSize, filterFpp);
    }

    @Override
    public boolean modifyConfig(long size, double fpp) {
        if (size == 0 || fpp == 0) {
            return false;
        }
        filterSize = size;
        filterFpp = fpp;
        return true;
    }

    @Override
    public boolean put(String pattern) {
        return bloomFilter.put(pattern);
    }

    @Override
    public boolean put(List<String> patterns) {
        patterns.forEach(x-> put(x));
        return true;
    }

    @Override
    public boolean match(String patch) {
        return bloomFilter.mightContain(patch);
    }


}
