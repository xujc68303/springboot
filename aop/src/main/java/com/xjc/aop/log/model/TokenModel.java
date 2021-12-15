package com.xjc.aop.log.model;

import java.io.Serializable;

/**
 * TokenModel.java
 *
 * @author Xujc
 * @date 2021/12/8
 */
public class TokenModel implements Serializable {

    private String token;
    private String expireTime;
    private String tokenType;

    public TokenModel() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
