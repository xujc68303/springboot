package com.util.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Version 1.0
 * @ClassName FairSyncLock
 * @Author jiachenXu
 * @Date 2020/9/29 21:58
 * @Description
 */
@Slf4j
public class FairSyncLock implements Runnable {

    private String service;

    private Lock lock;

    public FairSyncLock(String service, Lock lock) {
        this.service = service;
        this.lock = lock;
    }

    public String getService() {
        return service;
    }

    public Lock getLock() {
        return lock;
    }


    @Override
    public void run() {
        try {
            lock.tryLock(30, TimeUnit.SECONDS);
            log.warn("service={} lock success ...", service);
        } catch (Exception e) {
            log.error("FairSyncLock error", e);
        } finally {
            lock.unlock( );
        }
    }

    public static void main(String[] args) {
        Lock lock = new ReentrantLock(true);
        FairSyncLock t1 = new FairSyncLock("t1", lock);
        FairSyncLock t2 = new FairSyncLock("t2", lock);
        FairSyncLock t3 = new FairSyncLock("t3", lock);
        t1.run( );
        t2.run( );
        t3.run( );
    }
}
