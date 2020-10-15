package com.xjc.mysql.mapper.dao;

import com.xjc.mysql.mapper.dataobject.QuartzJobDO;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName JobDao
 * @Author jiachenXu
 * @Date 2020/5/23
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
