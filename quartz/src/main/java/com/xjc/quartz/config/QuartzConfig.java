package com.xjc.quartz.config;

import lombok.extern.slf4j.Slf4j;
import net.sf.jabb.quartz.AutowiringSpringBeanJobFactory;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * @Version 1.0
 * @ClassName QuartzConfig
 * @Author jiachenXu
 * @Date 2020/5/4
 * @Description
 */
@Slf4j
@Configuration
public class QuartzConfig {

    @Autowired
    private volatile ApplicationContext applicationContext;

    private volatile Scheduler scheduler;

    @Bean
    public Scheduler scheduler() {
        synchronized (SchedulerFactoryBean.class) {
            SchedulerFactoryBean schedulerFactoryBean = schedulerFactoryBean(jobFactory(applicationContext));
            if (schedulerFactoryBean != null) {
                scheduler = schedulerFactoryBean.getScheduler();
            }
        }
        return scheduler;
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //将spring管理job自定义工厂交由调度器维护
        schedulerFactoryBean.setJobFactory(jobFactory);
        //设置配置文件位置
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("/application.yml"));
        //设置覆盖已存在的任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //项目启动完成后，等待1秒后开始执行调度器初始化
        schedulerFactoryBean.setStartupDelay(1);
        //设置调度器自动运行
        schedulerFactoryBean.setAutoStartup(true);
        //设置数据源，使用与项目统一数据源
        schedulerFactoryBean.setDataSource(applicationContext.getBean(DataSource.class));
        return schedulerFactoryBean;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

}