package com.xjc.aop.log.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CookieUtil.java
 *
 * @author Xujc
 * @date 2021/12/9
 */
public class CookieUtil {

    /**
     * 返回一个可用的 cookie
     *
     * @param request
     * @param response
     * @return
     */
    public static void getCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request != null && response != null) {
            Cookie cookie = new Cookie("token", request.getParameter("token"));
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setPath("/");
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
        }
    }

    public static void delCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request != null && response != null) {
            Cookie cookie = new Cookie("token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }

    public static boolean checkCookie(HttpServletRequest request) {
        boolean CHECKCOOKIE = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID2")) { //删除cookie
                    CHECKCOOKIE = true;
                }
            }
        }
        return CHECKCOOKIE;
    }
}
