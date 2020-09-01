package com.xjc.zookeeper.object;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * @Version 1.0
 * @ClassName InstanceDetails
 * @Author jiachenXu
 * @Date 2020/8/27 21:45
 * @Description zookeeper注册服务模型
 */
public class InstanceDetails implements Serializable {

    private static final long serialVersionUID = -7906097491203950825L;

    private String serviceDesc;

    /**
     * 服务列表
     */
    private Map<String, Object> maps = Maps.newHashMap( );

    public InstanceDetails() {
    }

    public InstanceDetails(String serviceDesc, Map<String, Object> maps) {
        this.serviceDesc = serviceDesc;
        this.maps = maps;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public Map<String, Object> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Object> maps) {
        this.maps = maps;
    }

    @Override
    public String toString() {
        return super.toString( );
    }
}
