package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Ll
 * @description: 限流工具
 * @date 2024/7/13 上午9:38
 */
@Component
public class FlowUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;;
    /**
     * @description: 限制发送的时间间距
     * @param: [key, blockTime]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/13 下午1:42
     */
    public boolean limitOnceCheck(String key , int blockTime){
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            return false;
        }else{
            stringRedisTemplate.opsForValue().set(key,"",blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
