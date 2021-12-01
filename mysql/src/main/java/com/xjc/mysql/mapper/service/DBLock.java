package com.xjc.mysql.mapper.service;

import com.xjc.mysql.mapper.dao.HostLockMapper;
import com.xjc.mysql.mapper.dataobject.HostLockDO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

/**
 * @Version 1.0
 * @ClassName DBLock
 * @Author jiachenXu
 * @Date 2020/9/29
 * @Description 基于DB头锁
 */
@Slf4j
@Service
public class DBLock {

    @Autowired
    private HostLockMapper hostLockMapper;

    public boolean queryLock(String lockName, int heartbeatTimeoutMinutes) {
        HostLockDO hostLockDO = hostLockMapper.getByNameAndEnv(lockName, "DEFAULT");
        if (Objects.isNull(hostLockDO)) {
            //未初始化
            return initLock(lockName);
        }
        if (hostLockDO.getRunHost().equals("当前机器host")) {
            //只有本地持有
            return true;
        }
        if (heartbeatTimeoutMinutes <= 0) {
            // skip
            return false;
        }
        Date allowWindiwStartTime = LocalDateTime.now().minusMinutes(heartbeatTimeoutMinutes).toDate();
        Timestamp lastTime = hostLockDO.getLastTime();
        if (Objects.nonNull(lastTime) && lastTime.getTime() > allowWindiwStartTime.getTime()) {
            // 当前不为空，且最新心跳大于 说明未超时不能抢占
            return false;
        }
        // 尝试抢占
        hostLockDO.setLastHost("当前机器host");
        // 锁没有超时 不会更新
        int i = hostLockMapper.updateHost(hostLockDO.getLastHost(), hostLockDO.getExecutorName(), hostLockDO.getEnv(), heartbeatTimeoutMinutes);
        if (i <= 0) {
            log.warn("抢占失败");
            return false;
        }
        log.info("抢占成功");
        return true;
    }

    public boolean refreshLock(String lockName) {
        HostLockDO hostLockDO = new HostLockDO();
        hostLockDO.setLastHost("当前机器host");
        hostLockDO.setExecutorName(lockName);
        hostLockDO.setEnv("DEFAULT");
        hostLockDO.setRunHost("当前机器host");
        return hostLockMapper.updateLastTime(hostLockDO) > 0;
    }

    public void updateValidHost(String lockName, String validHost) {
        HostLockDO hostLockDO = new HostLockDO();
        hostLockDO.setLastHost("当前机器host");
        hostLockDO.setExecutorName(lockName);
        hostLockDO.setEnv("DEFAULT");
        if (validHost != null) {
            hostLockDO.setRunHost(validHost);
        } else {
            hostLockDO.setRunHost("当前机器host");
        }
        hostLockMapper.updateRunHost(hostLockDO);
    }

    private boolean initLock(String lockName) {
        HostLockDO hostLockDO = new HostLockDO();
        hostLockDO.setLastHost("机器码");
        hostLockDO.setExecutorName(lockName);
        hostLockDO.setEnv("DEFAULT");
        int i = hostLockMapper.insertSelective(hostLockDO);
        if (i <= 0) {
            log.error("lock init error");
            return false;
        }
        return true;
    }
}
