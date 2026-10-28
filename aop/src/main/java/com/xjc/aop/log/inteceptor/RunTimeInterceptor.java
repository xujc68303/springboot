package com.xjc.aop.log.inteceptor;

import com.alibaba.fastjson.JSON;
import com.sun.istack.internal.NotNull;
import com.xjc.aop.log.config.annotation.RunTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
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
public class RunTimeInterceptor {

    private static final ThreadLocal<StopWatch> STOP_WATCH_THREAD_LOCAL = new ThreadLocal<>();

    @Pointcut("@annotation(com.xjc.aop.log.config.annotation.RunTime)")
    public void interceptor() {
    }

    @Before(value = "interceptor() && @annotation(runTime)")
    public void beforeService(@NotNull JoinPoint point, @NotNull RunTime runTime) {
        String service = runTime.value();
        StopWatch stopWatch = new StopWatch(service);
        stopWatch.start();
        STOP_WATCH_THREAD_LOCAL.set(stopWatch);
        log.info("服务{}调用开始, request:{}", service, JSON.toJSONString(point.getArgs()));
    }

    @Around(value = "interceptor() && @annotation(runTime)")
    public Object aroundService(ProceedingJoinPoint pjp, RunTime runTime) throws Throwable {
        log.info("服务{}调用中,", runTime.value());
        return pjp.proceed();
    }

    @AfterReturning(value = "interceptor() && @annotation(runTime)", returning = "rvt")
    public void after(RunTime runTime, Object rvt) {
        try {
            StopWatch stopWatch = STOP_WATCH_THREAD_LOCAL.get();
            if (stopWatch != null && stopWatch.isRunning()) {
                stopWatch.stop();
                log.info("服务{}调用结束, 耗时:{}ms", runTime.value(), stopWatch.getTotalTimeMillis());
            }
        } finally {
            STOP_WATCH_THREAD_LOCAL.remove();
        }
    }
}
