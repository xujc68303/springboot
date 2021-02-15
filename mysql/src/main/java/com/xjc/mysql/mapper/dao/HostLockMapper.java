package com.xjc.mysql.mapper.dao;

import org.apache.ibatis.annotations.Param;

import com.xjc.mysql.mapper.dataobject.HostLockDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Version 1.0
 * @ClassName HostLockMapper
 * @Author jiachenXu
 * @Date 2020/9/29
 * @Description
 */
@Mapper
public interface HostLockMapper {

    int insertSelective(HostLockDO hostLockDO);

    int updateLastTime(HostLockDO hostLockDO);

    int updateRunHost(HostLockDO hostLockDO);

    HostLockDO getByNameAndEnv(@Param("executorName") String executorName, @Param("env") String env);

    int updateHost(@Param("lastHost") String lastHost, @Param("executorName") String executorName,
                   @Param("env") String env, @Param("heartbeatTimeoutMinutes") int heartbeatTimeoutMinutes);

}
