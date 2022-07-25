package com.xjc.redis;

import com.alibaba.fastjson.JSON;
import com.xjc.redis.api.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(value = "/test")
public class Test {

    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/add")
    public void add(@RequestParam("accountId") Long accountId, @RequestParam("mobile") String mobile) {
        String key = "account_rsh";
        redisService.zAdd(key, String.valueOf(accountId), Long.parseLong(mobile));
    }

    @GetMapping(value = "/getByAccountId")
    public void getByAccountId(@RequestParam("accountId") Long accountId) {
        String key = "account_rsh";
        Long aLong = redisService.zRank(key, String.valueOf(accountId));
        System.out.println(aLong);
    }

    @GetMapping(value = "/getByMobile")
    public void getByMobile(@RequestParam("mobile") String mobile) {
        String key = "account_rsh";
        Set<String> strings = redisService.zRangeByScore(key, Long.parseLong(mobile), Long.parseLong(mobile));
        System.out.println(JSON.toJSONString(strings));
    }

}
