package com.winter.utils;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Winter
 * @date 2023/12/20 15:27
 */
@Component
public class JWTUtil {
    /**
     * 根据字符串数据生成token
     * @param data 数据
     * @param expireTime 过期时间，单位毫秒
     * @param secret 密钥
     * @return token
     */
    public static String sign(String data, long expireTime, String secret) {
        Date date = new Date(System.currentTimeMillis() + expireTime);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("body", data)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 根据对象数据生成token
     * @param data 数据对象
     * @param expireTime 过期时间，单位毫秒
     * @param secret 密钥
     * @return token
     */
    public static <T> String sign(T data, long expireTime, String secret) {
        Date date = new Date(System.currentTimeMillis() + expireTime);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String jsonStr = JSON.toJSONString(data);
        return JWT.create()
                .withClaim("body", jsonStr)
                .withExpiresAt(date)
                .sign(algorithm);
    }
    /**
     * 解析token并将数据转换为指定类的对象
     * @param token token
     * @param clazz 数据类
     * @param <T> 数据类型
     * @return 数据
     */
    public static <T> T getPayload(String token, Class<T> clazz) {
        DecodedJWT jwt = JWT.decode(token);
        String jsonStr = jwt.getClaim("body").asString();
        return JSON.parseObject(jsonStr, clazz);
    }
    /**
     * 解析token并将数据转换为字符串
     * @param token token
     * @return 数据
     */
    public static String getPayload(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("body").asString();
    }
    /**
     * 获取token过期时间
     * @param token token
     * @return 过期时间，单位毫秒
     */
    public static Long getExpireTime(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime();
    }
     /**
     * 验证token是否过期
     * @param token token
     * @param secret 密钥
     * @return 是否过期  true：未过期 false：过期
     */
    public static boolean verify(String token, String secret) {
        if (token == null) {
            return false;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
