package com.xjc.aop.log.inteceptor;

import com.alibaba.fastjson.JSON;
import com.sun.istack.internal.NotNull;
import com.xjc.aop.log.annotation.RunTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @Version 1.0
 * @ClassName RunTimeInteceptor
 * @Author jiachenXu
 * @Date 2020/8/29
 * @Description
 */
@Slf4j
@Aspect
@Component
public class RunTimeInteceptor {

    private StopWatch stopWatch;

    private long startTime;

    @Pointcut("@annotation(com.xjc.aop.log.annotation.RunTime)")
    public void inteceptor() {
    }

    @Before(value = "inteceptor()")
    public void beforeService(@NotNull JoinPoint point, @NotNull RunTime runTime) {
        String service = runTime.value( );
        stopWatch = new StopWatch(service);
        stopWatch.start();
        startTime = stopWatch.getTotalTimeMillis();
        log.info("服务{}调用开始, request:{}", service, JSON.toJSONString(point.getArgs( )));
    }

    @Around(value = "inteceptor()")
    public void aroundService(@NotNull RunTime runTime) {
        log.info("服务{}调用中,", runTime.value( ));
    }

    @AfterReturning(value = "inteceptor()", returning = "rvt")
    public void after(@NotNull RunTime runTime, Object rvt) {
        if(stopWatch.isRunning()){
            stopWatch.stop();
            long endTime = stopWatch.getTotalTimeMillis();
            log.info("服务{}调用结束, 耗时:{}", runTime.value( ), (endTime-startTime));
        }
    }
}
