package com.example.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ll
 * @description: 缓存工具
 * @date 2024/7/20 下午2:08
 */
@Component
public class CacheUtils {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public <T> List<T> getListFromCache(String key, Class<T> clazz) {
        String s = stringRedisTemplate.opsForValue().get(key);
        if (s == null) { return null; }
        return JSONArray.parseArray(s).toList(clazz);
    }

    public <T> T getFromCache(String key, Class<T> clazz) {
        String s = stringRedisTemplate.opsForValue().get(key);
        if (s == null) { return null;}
        return JSONObject.parseObject(s).to(clazz);
    }

    public <T> void saveListToCache(String key, List<T> list, long expire) {
        stringRedisTemplate.opsForValue().set(key, JSONArray.from(list).toJSONString(), expire, TimeUnit.SECONDS);
    }

    public <T> void saveToCache(String key, T data, long expire) {
        stringRedisTemplate.opsForValue().set(key , JSONObject.toJSONString(data) , expire, TimeUnit.SECONDS);
    }

    public void deleteFromCache(String key) {
        stringRedisTemplate.delete(key);
    }
}
