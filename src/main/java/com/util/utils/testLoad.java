package com.util.utils;

import com.github.pagehelper.PageInfo;
import com.util.utils.down.DownLoadUtil;
import com.util.utils.executor.service.AsyncService;
import com.util.utils.file.FilesUtil;
import com.util.utils.quartz.QuartzExecuteService;
import com.util.utils.redis.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
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

    @Autowired
    private QuartzExecuteService quartzExecuteService;

    @RequestMapping("/lock")
    public Boolean lock() {
        return cacheUtil.distributedLock("xjc", "asdasd", "NX", 60L, TimeUnit.MINUTES);
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
    public Object get() {
        return cacheUtil.get("xjc");
    }

    @RequestMapping("/delete")
    public Object delete() {
        return cacheUtil.delete("xjc");
    }

    @RequestMapping("/rename")
    public Object rename() {
        return cacheUtil.renameByKey("xjc", "xujiachen");
    }

    @RequestMapping("/setPermanentByKey")
    public Boolean setPermanentByKey() {
        return cacheUtil.setPermanentByKey("xjc");
    }

    @RequestMapping("/testAsync")
    public void testAsync() {
        asyncService.executeAsnc( );
    }

    @RequestMapping("/doTask1")
    public void doTask1() throws InterruptedException, ExecutionException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Future<String> result1 = asyncService.task1(countDownLatch);
        Future<String> result2 = asyncService.task2(countDownLatch);
        countDownLatch.await( );
        log.info("result1 = " + result1.get( ));
        log.info("result2 = " + result2.get( ));
        log.info("doTask1 end");
    }


    @RequestMapping("/addJob")
    public boolean addJob() throws SchedulerException, IOException {
        return quartzExecuteService.add("test1", "test", "0/1 * * * * ?", Task.class);
    }

    @RequestMapping("/modifyJob")
    public boolean modifyJob() throws SchedulerException, IOException {
        return quartzExecuteService.modify("test1", "test", "0/5 * * * * ?");
    }

    @RequestMapping("/deleteJob")
    public boolean deleteJob() throws SchedulerException, IOException {
        return quartzExecuteService.delete("test1", "test");
    }

    @RequestMapping("/pauseJob")
    public boolean pauseJob() throws SchedulerException, IOException {
        return quartzExecuteService.pause("test1", "test");
    }

    @RequestMapping("/resumeJob")
    public boolean resumeJob() throws SchedulerException, IOException {
        return quartzExecuteService.resume("test1", "test");
    }

    @RequestMapping("/pauseAll")
    public boolean pauseAll() throws SchedulerException, IOException {
        return quartzExecuteService.pauseAll( );
    }

    @RequestMapping("/getJob")
    public PageInfo getJob() {
        return quartzExecuteService.getJob("test1", 1, 10);
    }

    @RequestMapping("/resumeAll")
    public boolean resumeAll() throws SchedulerException, IOException {
        return quartzExecuteService.resumeAll( );
    }

    @RequestMapping("/give")
    public void give(@RequestParam(value = "offset", required = false) long offset) {
        cacheUtil.setBit("bit", offset, true);
    }

    @RequestMapping("/count")
    public long count() {
        return cacheUtil.bitCount("give");
    }

    @RequestMapping("/set")
    public Boolean set() {
        return cacheUtil.setWithExpire("adad", "dad", 3000, TimeUnit.MINUTES);
    }

    @RequestMapping("/zsetAdd")
    public void zsetAdd(String user, Integer i) {
        cacheUtil.zsetAdd("xjcLike", user, i);
    }

    @RequestMapping("/zsetDel")
    public Boolean zsetDel(String user) {
        return cacheUtil.zsetDel("xjcLike", user);
    }

    @RequestMapping("/reverseRank")
    public Long reverseRank(String user) {
        return cacheUtil.reverseRank("xjcLike", user);
    }

    @RequestMapping("/zsetRever")
    public Set<String> zsetRever() {
        return cacheUtil.zsetRever("xjcLike", 0, -1);
    }


}
