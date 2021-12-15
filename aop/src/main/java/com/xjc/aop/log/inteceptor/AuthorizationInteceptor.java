package com.xjc.aop.log.inteceptor;

import com.xjc.aop.log.annotation.Authorization;
import com.xjc.aop.log.model.UserInfo;
import com.xjc.aop.log.util.CookieUtil;
import com.xjc.aop.log.util.TokenUtil;
import com.xjc.aop.log.util.UserInfoHelp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Description AuthorizationInteceptor
 * @Date 2021/8/16 11:50
 * @Created by Xujc
 */
@Slf4j
@Aspect
@Component
public class AuthorizationInteceptor {

    @Pointcut("@annotation(com.xjc.aop.log.annotation.Authorization)")
    public void interceptor() {
    }

    @Around("interceptor()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            Authorization annotation = method.getAnnotation(Authorization.class);
            if (annotation != null) {
                if (!checkToken()) {
                    return "登陆超时，请重新登陆";
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            UserInfoHelp.remove();
        }
        return pjp.proceed();
    }

    private boolean checkToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) return false;
        UserInfo userInfo = null;
        try {
            userInfo = TokenUtil.validateToken(token);
            if (userInfo == null) {
                return false;
            }
        } catch (Exception e) {
            log.info("token校验失败");
            return false;
        }

        // redis service

        UserInfoHelp.add(userInfo);
        CookieUtil.getCookie(request, response);
        return true;

    }


}
