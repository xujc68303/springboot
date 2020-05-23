package com.util.utils.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Version 1.0
 * @ClassName QuartzConfig
 * @Author jiachenXu
 * @Date 2020/5/4 16:14
 * @Description
 */
@Configuration
public class QuartzConfig implements CommandLineRunner {

    @Bean
    public Scheduler getSchduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory( );
        return schedulerFactory.getScheduler( );
    }

    @Override
    public void run(String... args) throws Exception {
        // init
    }
}
