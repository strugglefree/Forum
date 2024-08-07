package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Ll
 * @description: JWT令牌工具类
 * @date 2024/7/11 下午2:54
 */

@Component
public class JWTUtils {
    @Value("qwertyuiop")
    private String key;
    @Value("7")
    private int expire;
    @Resource
    StringRedisTemplate template;

    @Resource
    FlowUtils utils;
/**
 * @description:  是不是有效的jwt
 * @param: [token]
 * @return: boolean
 * @author Ll
 * @date: 2024/7/11 下午7:39
 */
    public boolean invalidateJwt(String headerToken) {
        String token = this.convertToken(headerToken);
        if(token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id,jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }
/**
 * @description: 将令牌id和过期时间放入黑名单
 * @param: [uuid, date]
 * @return: boolean
 * @author Ll
 * @date: 2024/7/11 下午7:25
 */
    private boolean deleteToken(String uuid , Date date) {
        if(this.isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(date.getTime() - now.getTime(),0);
        template.opsForValue().set(Const.JWT_BAN_LIST + uuid,"",expire, TimeUnit.MILLISECONDS);
        return true;
    }
/**
 * @description: 查看对应令牌的id是否在黑名单内
 * @param: [uuid]
 * @return: boolean
 * @author Ll
 * @date: 2024/7/11 下午7:24
 */
    private boolean isInvalidToken(String uuid){
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BAN_LIST + uuid));
    }

     /**
     * @description:  检查令牌是否正确
     * @param: [token]
     * @return: com.auth0.jwt.interfaces.DecodedJWT
     * @author Ll
     * @date: 2024/7/11 下午4:24
     */
    public DecodedJWT resolveJwt(String headerToken) {
        String token = convertToken(headerToken);
        if(token == null) return null;
        else {
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTVerifier verifier = JWT.require(algorithm).build();
            try {
                DecodedJWT jwt = verifier.verify(token);
                if(this.isInvalidToken(jwt.getId())) return null;
                Date expiresAt = jwt.getExpiresAt();
                return new Date().after(expiresAt) ? null : jwt;
            }catch (JWTVerificationException e) {
                return null;
            }

        }
    }
    /**
     * @description:  创建一个JWT令牌
     * @param: [userDetails, id, name]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/11 下午4:24
     */
    public String createJWT(UserDetails userDetails , int id , String name){
        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("username",name)
                .withClaim("authorities",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(ExpireTime())
                .sign(algorithm);
    }
    /**
     * @description: 设置令牌过期时间
     * @param: []
     * @return: java.util.Date
     * @author Ll
     * @date: 2024/7/11 下午4:24
     */
    public Date ExpireTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }
    /**
     * @description:  转化Token
     * @param: [headerToken]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/11 下午4:25
     */
    private String convertToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer ")){
            return null;
        }
        return headerToken.substring(7);
    }
/**
 * @description: 获取ID
 * @param: [jwt]
 * @return: java.lang.Integer
 * @author Ll
 * @date: 2024/7/11 下午5:20
 */
    public Integer toID(DecodedJWT jwt){
        Map<String,Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

/**
 * @description:  用户信息
 * @param: [jwt]
 * @return: org.springframework.security.core.userdetails.UserDetails
 * @author Ll
 * @date: 2024/7/11 下午5:18
 */
    public UserDetails toUser(DecodedJWT jwt){
        Map<String,Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("username").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    private boolean frequencyCheck(int userId){
        String key = Const.JWT_FREQUENCY + userId;
        return utils.limitOnceUpgradeCheck(key, 30, 10, 300);
    }
}
