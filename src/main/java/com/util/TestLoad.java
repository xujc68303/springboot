//package com.util;
//
//import com.util.down.DownLoadUtil;
//import com.util.executor.service.AsyncService;
//import com.xjc.quartz.api.QuartzService;
//import com.xjc.redis.api.RedisService;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.SchedulerException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Set;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Version 1.0
// * @ClassName TestLoad
// * @Author jiachenXu
// * @Date 2020/3/6 16:06
// * @Description
// */
//@Slf4j
//@RestController
//public class TestLoad {
//
//    @Autowired
//    private DownLoadUtil loadUtil;
//
//    @Autowired
//    private RedisService redisService;
//
//    @Autowired
//    private AsyncService asyncService;
//
//    @Autowired
//    private QuartzService quartzService;
//
//    @RequestMapping("/lock")
//    public Boolean lock() {
//        return redisService.distributedLock("xjc", "asdasd", "NX", 60L, TimeUnit.MINUTES);
//    }
//
//    @RequestMapping("/unlock")
//    public Boolean unlock() {
//        return redisService.unlock("xjc", "asdasd");
//    }
//
//    @RequestMapping("/load")
//    public void load(HttpServletRequest request, HttpServletResponse response) {
//        loadUtil.downLoadFile(request, response);
//    }
//
//    @RequestMapping("/get")
//    public Object get() {
//        return redisService.get("xjc");
//    }
//
//    @RequestMapping("/delete")
//    public Object delete() {
//        return redisService.delete("xjc");
//    }
//
//    @RequestMapping("/rename")
//    public Object rename() {
//        return redisService.renameByKey("xjc", "xujiachen");
//    }
//
//    @RequestMapping("/setPermanentByKey")
//    public Boolean setPermanentByKey() {
//        return redisService.setPermanentByKey("xjc");
//    }
//
//    @RequestMapping("/testAsync")
//    public void testAsync() {
//        asyncService.executeAsnc( );
//    }
//
//    @RequestMapping("/doTask1")
//    public void doTask1() throws InterruptedException, ExecutionException {
//        CountDownLatch countDownLatch = new CountDownLatch(2);
//        Future<String> result1 = asyncService.task1(countDownLatch);
//        Future<String> result2 = asyncService.task2(countDownLatch);
//        countDownLatch.await( );
//        log.info("result1 = " + result1.get( ));
//        log.info("result2 = " + result2.get( ));
//        log.info("doTask1 end");
//    }
//
//    @RequestMapping("/addJob")
//    public boolean addJob() throws SchedulerException {
//        return quartzService.add("test1", "test", "0/1 * * * * ?", Task.class);
//    }
//
//    @RequestMapping("/modifyJob")
//    public boolean modifyJob() throws SchedulerException {
//        return quartzService.modify("test1", "test", "0/5 * * * * ?");
//    }
//
//    @RequestMapping("/deleteJob")
//    public boolean deleteJob() throws SchedulerException {
//        return quartzService.delete("test1", "test");
//    }
//
//    @RequestMapping("/pauseJob")
//    public boolean pauseJob() throws SchedulerException {
//        return quartzService.pause("test1", "test");
//    }
//
//    @RequestMapping("/resumeJob")
//    public boolean resumeJob() throws SchedulerException {
//        return quartzService.resume("test1", "test");
//    }
//
//    @RequestMapping("/pauseAll")
//    public boolean pauseAll() throws SchedulerException {
//        return quartzService.pauseAll( );
//    }
//
////    @RequestMapping("/getJob")
////    public PageInfo getJob() {
////        return quartzService.getJob("test1", 1, 10);
////    }
//
//    @RequestMapping("/resumeAll")
//    public boolean resumeAll() throws SchedulerException {
//        return quartzService.resumeAll( );
//    }
//
//    @RequestMapping("/give")
//    public void give(@RequestParam(value = "offset", required = false) long offset) {
//        redisService.setBit("bit", offset, true);
//    }
//
//    @RequestMapping("/count")
//    public long count() {
//        return redisService.bitCount("give");
//    }
//
//    @RequestMapping("/set")
//    public Boolean set() {
//        return redisService.setWithExpire("adad", "dad", 3000, TimeUnit.MINUTES);
//    }
//
//    @RequestMapping("/zsetAdd")
//    public void zsetAdd(String user, Integer i) {
//        redisService.zadd("xjcLike", user, i);
//    }
//
//    @RequestMapping("/zsetDel")
//    public Boolean zsetDel(String user) {
//        return redisService.zrem("xjcLike", user);
//    }
//
//    @RequestMapping("/reverseRank")
//    public Long reverseRank(String user) {
//        return redisService.zrevrank("xjcLike", user);
//    }
//
//    @RequestMapping("/zsetRever")
//    public Set<String> zsetRever() {
//        return redisService.zrevrange("xjcLike", 0, -1);
//    }
//
//
//}
