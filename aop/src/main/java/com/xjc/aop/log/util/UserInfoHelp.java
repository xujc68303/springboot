package com.xjc.aop.log.util;

import com.xjc.aop.log.model.UserInfo;

/**
 * UserInfoHelp.java
 *
 * @author Xujc
 * @date 2021/12/8
 */
public class UserInfoHelp {

    private static final ThreadLocal<UserInfo> userHolder = new ThreadLocal<>();

    public static void add(UserInfo userInfo) {
        userHolder.set(userInfo);
    }

    public static UserInfo getCurrentUser() {
        return userHolder.get();
    }

    public static void remove() {
        userHolder.remove();
    }
}
