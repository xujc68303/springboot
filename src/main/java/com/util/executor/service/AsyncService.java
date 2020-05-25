package com.util.executor.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @Version 1.0
 * @ClassName AsyncService
 * @Author jiachenXu
 * @Date 2020/4/8 21:55
 * @Description
 */
public interface AsyncService {

    void executeAsnc();

    Future<String> task1(CountDownLatch countDownLatch) throws InterruptedException;

    Future<String> task2(CountDownLatch countDownLatch) throws InterruptedException;

}
