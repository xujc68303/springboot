package com.datasource.mapper.dao;

import com.datasource.mapper.dataobject.QuartzJobDO;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName JobDao
 * @Author jiachenXu
 * @Date 2020/5/23 17:07
 * @Description
 */
public interface JobDao {

    /**
     * 定时任务查询
     *
     * @return
     */
    List<QuartzJobDO> queryJob(String jobName);
}
