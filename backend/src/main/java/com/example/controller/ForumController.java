package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ll
 * @description: 论坛内容的相关接口
 * @date 2024/7/18 下午1:19
 */

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService weatherService;

    @GetMapping("/weather")
    public RestBean<WeatherVO> getWeather(double longitude, double latitude) {
        WeatherVO vo = weatherService.GetLocationWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400,"获取天气信息失败，请联系管理员") : RestBean.success(vo);
    }


}
