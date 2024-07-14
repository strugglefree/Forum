package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Ll
 * @description: 限流工具
 * @date 2024/7/13 上午9:38
 */
@Component
public class FlowUtils {

    @Resource
    StringRedisTemplate template  ;
    /**
     * @description: 限制发送的时间间距
     * @param: [key, blockTime]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/13 下午1:42
     */
    public boolean limitOnceCheck(String key, int blockTime){
        return this.internalCheck(key, 1, blockTime, (overclock) -> false);
    }
    /**
     * @description: 内部使用请求限制主要逻辑
     * @param: [key, frequency, period, action]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/14 下午12:33
     */
    private boolean internalCheck(String key, int frequency, int period, LimitAction action){
        String count = template.opsForValue().get(key);
        if (count != null) {
            long value = Optional.ofNullable(template.opsForValue().increment(key)).orElse(0L);
            int c = Integer.parseInt(count);
            if(value != c + 1)
                template.expire(key, period, TimeUnit.SECONDS);
            return action.run(value > frequency);
        } else {
            template.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }
    /**
     * @description: 针对于单次频率限制，请求成功后，在冷却时间内不得再次进行请求 如3秒内不能再次发起请求，如果不听劝阻继续发起请求，将限制更长时间
     * @param: [key, frequency, baseTime, upgradeTime]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/14 下午12:34
     */
    public boolean limitOnceUpgradeCheck(String key, int frequency, int baseTime, int upgradeTime){
        return this.internalCheck(key, frequency, baseTime, (overclock) -> {
            if (overclock)
                template.opsForValue().set(key, "1", upgradeTime, TimeUnit.SECONDS);
            return false;
        });
    }
/**
 * @description: 针对于在时间段内多次请求限制，如3秒内限制请求20次，超出频率则封禁一段时间
 * @param: [counterKey, blockKey, blockTime, frequency, period]
 * @return: boolean
 * @author Ll
 * @date: 2024/7/14 下午12:35
 */
    public boolean limitPeriodCheck(String counterKey, String blockKey, int blockTime, int frequency, int period){
        return this.internalCheck(counterKey, frequency, period, (overclock) -> {
            if (overclock)
                template.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
            return !overclock;
        });
    }
    /**
     * @description: 限制行为与策略
     * @author Ll
     * @date: 2024/7/14 下午12:36
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }
}
