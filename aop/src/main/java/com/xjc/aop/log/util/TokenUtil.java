package com.xjc.aop.log.util;

import com.google.common.collect.Maps;
import com.xjc.aop.log.model.GenerateTokenParameter;
import com.xjc.aop.log.model.TokenModel;
import com.xjc.aop.log.model.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TokenUtil {

    private static final String SECRET = "token_secret";

    public static TokenModel generateToken(GenerateTokenParameter parameter) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, 60 * 60);
        Date expiresAt = c.getTime();
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("userId", parameter.getUserId());
        paramMap.put("uuid", parameter.getUuid());
        paramMap.put("phone", parameter.getPhone());
        paramMap.put("userName", parameter.getUserName());
        paramMap.put("nickName", parameter.getNickName());
        paramMap.put("userType", parameter.getUserType());
        paramMap.put("loginTime", parameter.getLoginTime());
        paramMap.put("expiredTime", parameter.getExpiredTime());
        String token = Jwts.builder()
                .setClaims(paramMap)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken("Bearer".concat(" ".concat(token)));
        tokenModel.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiresAt));
        tokenModel.setTokenType("Bearer");
        return tokenModel;
    }

    public static UserInfo validateToken(String token) {
        if (token == null) {
            return null;
        }
        Map<String, Object> tokenRes = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace("Bearer", ""))
                .getBody();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(tokenRes.get("userId").toString());
        userInfo.setUuid(tokenRes.get("uuid").toString());
        userInfo.setPhone(tokenRes.get("phone").toString());
        userInfo.setUserName(tokenRes.get("userName").toString());
        userInfo.setNickName(tokenRes.get("nickName").toString());
        userInfo.setUserType(tokenRes.get("userType").toString());
        userInfo.setLoginTime((Date) tokenRes.get("loginTime"));
        userInfo.setExpiredTime((Date) tokenRes.get("expiredTime"));
        return userInfo;
    }
}
