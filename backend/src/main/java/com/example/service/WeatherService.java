package com.example.service;

import com.example.entity.vo.response.WeatherVO;

/**
 * @author Ll
 * @description: 天气模块的功能
 * @date 2024/7/18 下午1:28
 */
public interface WeatherService {
    WeatherVO GetLocationWeather(Double lon, Double lat);
}
