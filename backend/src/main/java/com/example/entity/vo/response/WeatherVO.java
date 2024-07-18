package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @author Ll
 * @description: 天气响应类
 * @date 2024/7/18 下午1:22
 */

@Data
public class WeatherVO {
    JSONObject location;
    JSONObject now;
    JSONArray hourly;
}
