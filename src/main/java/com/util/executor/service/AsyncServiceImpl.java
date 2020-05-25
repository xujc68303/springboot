package com.util.executor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @Version 1.0
 * @ClassName AsyncServiceImpl
 * @Author jiachenXu
 * @Date 2020/4/8 21:56
 * @Description
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    @Async("asyncServiceExecutor")
    @Override
    public void executeAsnc() {

        log.info("start executeAsync");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace( );
        }
        log.info("end executeAsync");
    }

    @Async("asyncServiceExecutor")
    @Override
    public Future<String> task1(CountDownLatch countDownLatch) throws InterruptedException {
        log.info("start task1");
        Thread.sleep(5000);
        log.info("end task1");
        countDownLatch.countDown( );
        return new AsyncResult<String>("Task1 accomplished!");
    }

    @Override
    public Future<String> task2(CountDownLatch countDownLatch) throws InterruptedException {
        log.info("start task2");
        Thread.sleep(3000);
        log.info("end task2");
        countDownLatch.countDown( );
        return new AsyncResult<String>("Task2 accomplished!");
    }
}
