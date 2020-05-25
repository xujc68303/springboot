package com.util.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Version 1.0
 * @ClassName ExecutorConfig
 * @Author jiachenXu
 * @Date 2020/4/8 21:33
 * @Description 线程池配置
 */
@Slf4j
@EnableAsync
@Configuration
public class ExecutorConfig implements AsyncConfigurer {

    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;

    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;

    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;

    @Value("${async.executor.thread.name.prefix}")
    private String namePrefix;

    @Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(namePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 配置异常处理机制
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex,method,params)->{
            log.error("异步线程执行失败。方法：[{}],异常信息[{}] : ", method, ex.getMessage(),ex);
        };
    }

}
