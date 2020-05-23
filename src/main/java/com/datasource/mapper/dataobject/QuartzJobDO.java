package com.datasource.mapper.dataobject;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Version 1.0
 * @ClassName QuartzJobDO
 * @Author jiachenXu
 * @Date 2020/5/23 17:12
 * @Description
 */
@Data
@NoArgsConstructor
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

    public QuartzJobDO(String jobName, String jobGroup, String jobClassName, String cronExpression) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.jobClassName = jobClassName;
        this.cronExpression = cronExpression;
    }
}
