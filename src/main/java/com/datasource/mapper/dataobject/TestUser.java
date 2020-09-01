package com.datasource.mapper.dataobject;

import java.io.Serializable;

public class TestUser implements Serializable {

    private static final long serialVersionUID = 3580865635839853856L;

    private Long id;

    private String userName;

    private Integer userAge;

    private String userBase;

    public TestUser() {
    }

    public TestUser(Long id, String userName, Integer userAge, String userBase) {
        this.id = id;
        this.userName = userName;
        this.userAge = userAge;
        this.userBase = userBase;
    }

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
        this.userName = userName;
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
        this.userBase = userBase;
    }

    @Override
    public String toString() {
        return super.toString( );
    }
}