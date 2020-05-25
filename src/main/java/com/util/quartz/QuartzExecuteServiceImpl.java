package com.util.quartz;

import com.alibaba.excel.util.CollectionUtils;
import com.datasource.mapper.dao.JobDao;
import com.datasource.mapper.dataobject.QuartzJobDO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName QuartzExecuteServiceImpl
 * @Author jiachenXu
 * @Date 2020/5/4 15:21
 * @Description
 */
@Slf4j
@Service
public class QuartzExecuteServiceImpl implements QuartzExecuteService {

    @Autowired
    private QuartzConfig quartzConfig;

    @Autowired
    private JobDao jobDao;

    @Override
    public PageInfo getJob(String jobName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<QuartzJobDO> list = jobDao.queryJob(jobName);
        return new PageInfo<>(list);
    }

    @Override
    public Boolean add(String key, String group, String cron, Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = quartzConfig.getScheduler( );
        scheduler.start( );
        cron = cron.trim( );
        // 任务存在直接返回
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(key, group));
        if (!CollectionUtils.isEmpty(triggers) || checkCron(cron)) {
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
        return scheduler.scheduleJob(jobDetail, trigger) != null;
    }

    @Override
    public Boolean modify(String key, String group, String newCron) throws SchedulerException {
        Date result = null;
        if (checkCron(newCron)) {
            return false;
        }
        Scheduler scheduler = quartzConfig.getScheduler( );
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
    public Boolean delete(String key, String group) throws SchedulerException {
        if (isExist(key, group)) {
            return quartzConfig.getScheduler( ).deleteJob(new JobKey(key, group));
        }
        return false;
    }

    @Override
    public Boolean pause(String key, String group) throws SchedulerException {
        if (isExist(key, group)) {
            quartzConfig.getScheduler( ).pauseJob(new JobKey(key, group));
            return true;
        }
        return false;
    }

    @Override
    public Boolean resume(String key, String group) throws SchedulerException {
        if (isExist(key, group)) {
            quartzConfig.getScheduler( ).resumeJob(new JobKey(key, group));
            return true;
        }
        return false;
    }

    @Override
    public Boolean pauseAll() throws SchedulerException {
        quartzConfig.getScheduler( ).pauseAll( );
        return true;
    }

    @Override
    public Boolean resumeAll() throws SchedulerException {
        quartzConfig.getScheduler( ).resumeAll( );
        return true;
    }

    @Override
    public Boolean isExist(String key, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(key, group);
        return quartzConfig.getScheduler( ).getJobDetail(jobKey) != null;
    }

    /**
     * 检车corn表达式合法性
     *
     * @param cronExpression
     * @return
     */
    private Boolean checkCron(String cronExpression) {
        return !CronExpression.isValidExpression(cronExpression);
    }

}