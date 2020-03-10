package com.datasource.mapper.dataobject;

import java.io.Serializable;

public class TestUser implements Serializable {
    private static final long serialVersionUID = 3580865635839853856L;
    private Long id;

    private String userName;

    private Integer userAge;

    private String userBase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public String getUserBase() {
        return userBase;
    }

    public void setUserBase(String userBase) {
        this.userBase = userBase == null ? null : userBase.trim();
    }
}