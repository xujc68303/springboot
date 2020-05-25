package com.util;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalTime;

/**
 * @Version 1.0
 * @ClassName Task
 * @Author jiachenXu
 * @Date 2020/5/4 16:22
 * @Description
 */
@Slf4j
@EnableScheduling
@DisallowConcurrentExecution
public class Task extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("执行任务"+LocalTime.now( ).toString( ));
    }

}