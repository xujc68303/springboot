package com.xjc.mysql.mapper.dataobject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Version 1.0
 * @ClassName QuartzJobDO
 * @Author jiachenXu
 * @Date 2020/5/23
 * @Description
 */
public class QuartzJobDO implements Serializable {

    private static final long serialVersionUID = -3893865535056305084L;

    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 执行类
     */
    private String jobClassName;

    /**
     * cron
     */
    private String cronExpression;

    /**
     * 执行时间
     */
    private String triggerName;

    /**
     * 任务状态
     */
    private String triggerState;

    /**
     * 任务名称 用于修改
     */
    private String oldJobName;

    /**
     * 任务分组 用于修改
     */
    private String oldJobGroup;

    /**
     * 方便业务job中进行数据库操作
     */
    private List<Map<String, Object>> jobDataParam;

    public QuartzJobDO() {
    }

    public QuartzJobDO(String jobName, String jobGroup, String jobClassName, String cronExpression) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.jobClassName = jobClassName;
        this.cronExpression = cronExpression;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    public String getOldJobName() {
        return oldJobName;
    }

    public void setOldJobName(String oldJobName) {
        this.oldJobName = oldJobName;
    }

    public String getOldJobGroup() {
        return oldJobGroup;
    }

    public void setOldJobGroup(String oldJobGroup) {
        this.oldJobGroup = oldJobGroup;
    }

    public List<Map<String, Object>> getJobDataParam() {
        return jobDataParam;
    }

    public void setJobDataParam(List<Map<String, Object>> jobDataParam) {
        this.jobDataParam = jobDataParam;
    }

    @Override
    public String toString() {
        return super.toString( );
    }
}
