package com.xjc.zookeeper.object;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @Version 1.0
 * @ClassName InstanceDetails
 * @Author jiachenXu
 * @Date 2020/8/27 21:45
 * @Description
 */
@Data
@AllArgsConstructor
public class InstanceDetails {

    private String serviceDesc;

    private Map<String, Object> maps = Maps.newHashMap( );
}
