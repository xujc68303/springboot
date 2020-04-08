package com.util.utils;

import com.util.utils.down.DownLoadUtil;
import com.util.utils.executor.service.AsyncService;
import com.util.utils.file.FilesUtil;
import com.util.utils.redis.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Version 1.0
 * @ClassName testLoad
 * @Author jiachenXu
 * @Date 2020/3/6 16:06
 * @Description JDK7新特性，更快捷创建、读取文件
 */
@Slf4j
@RestController
public class testLoad {

    @Autowired
    private DownLoadUtil loadUtil;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private FilesUtil filesUtil;

    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/lock")
    public Boolean lock() {
        return cacheUtil.distributedLock("xjc", "asdasd","NX", 60L, TimeUnit.MINUTES);
    }

    @RequestMapping("/unlock")
    public Boolean unlock() {
        return cacheUtil.unlock("xjc", "asdasd");
    }

    @RequestMapping("/load")
    public void load(HttpServletRequest request, HttpServletResponse response) {
        loadUtil.downLoadFile(request, response);
    }

    @RequestMapping("/redis")
    public Boolean redis() {
        return cacheUtil.setWithExpire("xjc", "test", 60L, TimeUnit.MINUTES);
    }

    @RequestMapping("/get")
    public Object get(){
        return cacheUtil.get("xjc");
    }

    @RequestMapping("/delete")
    public Object delete(){
        return cacheUtil.delete("xjc");
    }

    @RequestMapping("/rename")
    public Object rename(){
        return cacheUtil.renameByKey("xjc","xujiachen");
    }

    @RequestMapping("/setPermanentByKey")
    public Boolean setPermanentByKey(){
        return cacheUtil.setPermanentByKey("xjc");
    }

    @RequestMapping("/testAsync")
    public void testAsync(){
        asyncService.executeAsnc();
    }

    @RequestMapping("/doTask1")
    public void doTask1() throws InterruptedException, ExecutionException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Future<String> result1 = asyncService.task1(countDownLatch);
        Future<String> result2 = asyncService.task2(countDownLatch);
        countDownLatch.await();
        log.info("result1 = " + result1.get());
        log.info("result2 = " + result2.get());
        log.info("doTask1 end");
    }

}
