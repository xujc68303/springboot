package com.xjc.mysql.mapper.dataobject;

import lombok.Data;

@Data
public class TestUser {
    private static final long serialVersionUID = 3580865635839853856L;
    /**
     * 主键id
     */
    private Long id;

    /**
     * 名称
     */
    private String userName;

    /**
     * 年龄
     */
    private Integer userAge;

    /**
     * 地址
     */
    private String userBase;

    public static final String COL_ID = "id";

    public static final String COL_USER_NAME = "user_name";

    public static final String COL_USER_AGE = "user_age";

    public static final String COL_USER_BASE = "user_base";
}