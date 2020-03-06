package com.util.utils;

import com.util.utils.redis.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Version 1.0
 * @ClassName testLoad
 * @Author xujiachen
 * @Date 2020/3/6 16:06
 * @Description TODO
 */
@RestController
public class testLoad {

    @Autowired
    private DownLoadUtil loadUtil;

    @Autowired
    private CacheUtil cacheUtil;

    @RequestMapping("/load")
    public void load(HttpServletRequest request, HttpServletResponse response){
        loadUtil.downLoadFile(request, response);
    }


    @RequestMapping("/redis")
    public Boolean redis(){
        return cacheUtil.setWithExpire("xjc", "test", 30);
    }



}
