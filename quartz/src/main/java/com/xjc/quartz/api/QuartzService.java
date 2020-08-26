package com.xjc.quartz.api;

import com.github.pagehelper.PageInfo;
import org.quartz.Job;
import org.quartz.SchedulerException;

/**
 * @Version 1.0
 * @ClassName QuartzService
 * @Author jiachenXu
 * @Date 2020/5/4 15:54
 * @Description 定时任务执行
 */
public interface QuartzService {

    /**
     * 获取定时任务
     *
     * @param jobName
     * @param pageNum
     * @param pageSize
     * @return
     */
//    PageInfo getJob(String jobName, Integer pageNum, Integer pageSize);

    /**
     * 添加定时任务
     *
     * @param key      key
     * @param group    分区
     * @param cron     表达式
     * @param jobClass 执行类
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean add(String key, String group, String cron, Class<? extends Job> jobClass) throws SchedulerException;

    /**
     * 修改定时任务
     *
     * @param key     key
     * @param group   分区
     * @param newCron 表达式
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean modify(String key, String group, String newCron) throws SchedulerException;

    /**
     * 删除定时任务
     *
     * @param key   key
     * @param group 分区
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean delete(String key, String group) throws SchedulerException;

    /**
     * 暂停定时任务
     *
     * @param key   key
     * @param group 分区
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean pause(String key, String group) throws SchedulerException;

    /**
     * 恢复定时任务
     *
     * @param key   key
     * @param group 分区
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean resume(String key, String group) throws SchedulerException;

    /**
     * 暂停全部定时任务
     *
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean pauseAll() throws SchedulerException;

    /**
     * 恢复全部定时任务
     *
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean resumeAll() throws SchedulerException;

    /**
     * 定时任务是否存在
     *
     * @param key   key
     * @param group 分区
     * @return 执行结果
     * @throws SchedulerException
     */
    Boolean isExist(String key, String group) throws SchedulerException;

}