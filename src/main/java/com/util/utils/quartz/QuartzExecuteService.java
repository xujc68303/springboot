package com.util.utils.quartz;

import org.quartz.Job;
import org.quartz.SchedulerException;

import java.io.IOException;

/**
 * @Version 1.0
 * @ClassName QuartzExecuteService
 * @Author jiachenXu
 * @Date 2020/5/4 15:54
 * @Description 定时任务执行
 */
public interface QuartzExecuteService {

    /**
     * 添加定时任务
     *
     * @param key
     * @param group
     * @param cron
     * @param jobClass
     * @return
     * @throws SchedulerException
     */
    boolean add(String key, String group, String cron, Class<? extends Job> jobClass) throws SchedulerException, IOException;

    /**
     * 修改定时任务
     *
     * @param key
     * @param group
     * @param newCron
     * @return
     * @throws SchedulerException
     */
    boolean modify(String key, String group, String newCron) throws SchedulerException, IOException;

    /**
     * 删除定时任务
     *
     * @param key
     * @param group
     * @return
     * @throws SchedulerException
     */
    boolean delete(String key, String group) throws SchedulerException, IOException;

    /**
     * 暂停定时任务
     *
     * @param key
     * @param group
     * @return
     * @throws SchedulerException
     */
    boolean pause(String key, String group) throws SchedulerException, IOException;

    /**
     * 恢复定时任务
     *
     * @param key
     * @param group
     * @return
     * @throws SchedulerException
     */
    boolean resume(String key, String group) throws SchedulerException, IOException;

    /**
     * 暂停全部定时任务
     * @return
     * @throws SchedulerException
     */
    boolean pauseAll() throws SchedulerException, IOException;

    /**
     * 恢复全部定时任务
     * @return
     * @throws SchedulerException
     */
    boolean resumeAll() throws SchedulerException, IOException;

    /**
     * 定时任务是否存在
     *
     * @param key
     * @param group
     * @return
     * @throws SchedulerException
     */
    Boolean isExist(String key, String group) throws SchedulerException;

}