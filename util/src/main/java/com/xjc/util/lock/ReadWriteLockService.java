package com.xjc.util.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Version 1.0
 * @ClassName ReadWriteLockService
 * @Author jiachenXu
 * @Date 2020/9/29
 * @Description
 */
@Slf4j
@SuppressWarnings("all")
public class ReadWriteLockService {

    private final AtomicStampedReference<Long> money = new AtomicStampedReference<>(0L, 0);

    private volatile Long surplus;

    private volatile Integer recharge;

    public ReadWriteLockService() {
    }

    public ReadWriteLockService(Long surplus, Integer recharge) {
        this.surplus = surplus;
        this.recharge = recharge;
    }

    public boolean increase(Integer recharge) {
        if (recharge != null) {
            Long reference = getReference( );
            Integer stamp = getStamp( );

            if (money.compareAndSet(reference, reference + recharge, stamp, stamp + 1)) {
                if (recharge != 0) {
                    this.surplus = getSurplus( ) + recharge;
                    this.recharge = 0;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace( );
                    }
                    log.warn("充值成功！当前余额={}", getSurplus( ));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean minus(Integer minus) {
        if (getSurplus( ) >= minus) {
            Long reference = getReference( );
            Integer stamp = getStamp( );

            if (getSurplus( ) != 0) {
                if (money.compareAndSet(reference, reference - minus, stamp, stamp - 1)) {
                    this.surplus = getSurplus( ) - minus;
                    log.warn("消费成功！当前余额={}", getSurplus( ));
                    return true;
                }
            }
        }
        log.error("余额不足");
        return false;
    }

    public Long getSurplus() {
        return surplus;
    }

    public Integer getRecharge() {
        return recharge;
    }

    public Integer getStamp() {
        return money.getStamp( );
    }

    public Long getReference() {
        return money.getReference( );
    }

    public static void main(String[] args) {
        ReadWriteLock lock = new ReentrantReadWriteLock( );
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ReadWriteLockService readWriteLockService = new ReadWriteLockService( );
        Random buy = new Random(100);
        Random shopp = new Random(130);

        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {

                lock.writeLock( ).lock( );
                try {
                    readWriteLockService.increase(buy.nextInt(100));

                    lock.readLock( ).lock( );
                    try {
                        readWriteLockService.minus(shopp.nextInt(130));
                    } catch (Exception e) {
                    } finally {
                        lock.readLock( ).unlock( );
                    }
                } catch (Exception e) {
                } finally {
                    lock.writeLock( ).unlock( );
                }

            });
        }
    }

}
