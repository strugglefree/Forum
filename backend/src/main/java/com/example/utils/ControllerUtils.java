package com.example.utils;

import com.example.entity.RestBean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * @author Ll
 * @description: 接口返回工具类
 * @date 2024/7/19 下午2:37
 */
@Component
public class ControllerUtils {
    /**
     * @description: 针对于返回值为String作为错误信息的方法进行统一处理
     * @param: [action]
     * @return: com.example.entity.RestBean<java.lang.Void>
     * @author Ll
     * @date: 2024/7/14 上午9:28
     */
    public <T> RestBean<T> messageHandle(Supplier<String> action){
        String s = action.get();
        return s == null ? RestBean.success() : RestBean.failure(400,s);
    }
}
