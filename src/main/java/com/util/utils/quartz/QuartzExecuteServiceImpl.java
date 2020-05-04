package com.util.utils.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Version 1.0
 * @ClassName QuartzExecuteServiceImpl
 * @Author jiachenXu
 * @Date 2020/5/4 15:21
 * @Description
 */
@Service
@Slf4j
public class QuartzExecuteServiceImpl implements QuartzExecuteService {

    @Autowired
    private QuartzConfig quartzConfig;

    @Override
    public boolean add(String key, String group, String cron, Class<? extends Job> jobClass) throws SchedulerException {
        Date result = null;
        Scheduler scheduler = quartzConfig.getSchduler( );
        scheduler.start( );
        if (checkCron(cron)) {
            return false;
        }
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(key, group)
                .storeDurably( )
                .build( );
        Trigger trigger = TriggerBuilder.newTrigger( )
                .forJob(jobDetail)
                .withIdentity(key, group)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build( );
        result = scheduler.scheduleJob(jobDetail, trigger);
        return result != null;
    }

    @Override
    public boolean modify(String key, String group, String newCron) throws SchedulerException {
        Date result = null;
        if (checkCron(newCron)) {
            return false;
        }
        Scheduler scheduler = quartzConfig.getSchduler( );
        CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(key, group));
        String oldCron = oldTrigger.getCronExpression( );
        if (!oldCron.equals(newCron)) {
            CronTrigger cronTrigger = oldTrigger.getTriggerBuilder( )
                    .withIdentity(new TriggerKey(key, group))
                    .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
                    .build( );
            result = scheduler.rescheduleJob(oldTrigger.getKey( ), cronTrigger);
        }
        return result != null;
    }

    @Override
    public boolean delete(String key, String group) throws SchedulerException {
        Scheduler scheduler = quartzConfig.getSchduler( );
        if (isExist(key, group)) {
            return scheduler.deleteJob(new JobKey(key, group));
        }
        return false;
    }

    @Override
    public boolean pause(String key, String group) throws SchedulerException {
        Scheduler scheduler = quartzConfig.getSchduler( );
        if (isExist(key, group)) {
            scheduler.pauseJob(new JobKey(key, group));
            return true;
        }
        return false;
    }

    @Override
    public boolean resume(String key, String group) throws SchedulerException {
        Scheduler scheduler = quartzConfig.getSchduler( );
        if (isExist(key, group)) {
            scheduler.resumeJob(new JobKey(key, group));
            return true;
        }
        return false;
    }

    @Override
    public boolean pauseAll() throws SchedulerException {
        quartzConfig.getSchduler( ).pauseAll( );
        return true;
    }

    @Override
    public boolean resumeAll() throws SchedulerException {
        quartzConfig.getSchduler( ).resumeAll( );
        return true;
    }

    @Override
    public Boolean isExist(String key, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(key, group);
        return quartzConfig.getSchduler( ).getJobDetail(jobKey) != null;
    }

    private Boolean checkCron(String cronExpression) {
        return !CronExpression.isValidExpression(cronExpression);
    }

}
