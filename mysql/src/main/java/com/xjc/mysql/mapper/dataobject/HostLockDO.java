package com.xjc.mysql.mapper.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Version 1.0
 * @ClassName HostLockDO
 * @Author jiachenXu
 * @Date 2020/9/29
 * @Description
 */
@Data
public class HostLockDO implements Serializable {

    private Long id;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private String lastHost;

    private Timestamp lastTime;

    private String executorName;

    private String env;

    private String runHost;
}
