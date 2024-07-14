package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code , T data , String message) {
    public static <T> RestBean<T> success(T data) {
        return new RestBean<T>(200 , data , "请求成功");
    }

    public static <T> RestBean<T> success() {
        return success(null);
    }

    public static <T> RestBean<T> failure(int code,String message) {
        return new RestBean<T>(code, null , message);
    }

    public static <T> RestBean<T> unauthorized(String message) {
        return failure(401 , message);
    }

    public static <T> RestBean<T> accessDenied(String message) {
        return failure(403 , message);
    }

    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
