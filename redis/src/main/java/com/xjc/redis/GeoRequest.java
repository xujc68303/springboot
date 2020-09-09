package com.xjc.redis;

import java.io.Serializable;

/**
 * @Version 1.0
 * @ClassName GeoRequest
 * @Author jiachenXu
 * @Date 2020/9/9 21:24
 * @Description
 */
public class GeoRequest implements Serializable {

    private static final long serialVersionUID = -6581985347036131631L;

    private double longitude;

    private double latitude;

    private Object member;

    public GeoRequest() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Object getMember() {
        return member;
    }

    public void setMember(Object member) {
        this.member = member;
    }
}
