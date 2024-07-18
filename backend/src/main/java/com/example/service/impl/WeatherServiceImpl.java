package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * @author Ll
 * @description: 天气模块功能的具体实现
 * @date 2024/7/18 下午1:29
 */
@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    @Resource
    RestTemplate rest;
    @Resource
    StringRedisTemplate template;

    @Value("${spring.weather.key}")
    private String key;

    /**
     * @description: 通过经纬度获取天气情况
     * @param: [lon, lat]
     * @return: com.example.entity.vo.response.WeatherVO
     * @author Ll
     * @date: 2024/7/18 下午2:37
     */
    @Override
    public WeatherVO GetLocationWeather(Double lon, Double lat) {
        return fetchFromCache(lon, lat);
    }

    /**
     * @description: 检查缓存里面是否有对应的天气情况，有则取出天气，没有则通过API查询并加入到缓存中
     * @param: [lon, lat]
     * @return: com.example.entity.vo.response.WeatherVO
     * @author Ll
     * @date: 2024/7/18 下午2:34
     */
    private WeatherVO fetchFromCache(Double lon, Double lat) {
        JSONObject data = this.decompressStringToJson(
                rest.getForObject(
                        "https://geoapi.qweather.com/v2/city/lookup?location=" + lon + ',' + lat + "&key=" + key, byte[].class
                )
        );
        if(data == null) return null;
        log.info(data.toString());
        JSONObject location = data.getJSONArray("location").getJSONObject(0);
        String id = location.getString("id");
        String key = "weather:" + id;
        String cache = template.opsForValue().get(key);
        if(cache != null) {
            return JSONObject.parseObject(cache, WeatherVO.class);
        }
        WeatherVO vo = this.FindWeatherFromAPI(id, location);
        if(vo == null) {
            return null;
        }
        template.opsForValue().set(key,JSONObject.toJSONString(vo),1, TimeUnit.HOURS);
        return vo;
    }

    /**
     * @description: 从和风天气的API中获得天气数据
     * @param: [id, location]
     * @return: com.example.entity.vo.response.WeatherVO
     * @author Ll
     * @date: 2024/7/18 下午2:32
     */
    private WeatherVO FindWeatherFromAPI(String id,JSONObject location) {
        WeatherVO vo = new WeatherVO();
        vo.setLocation(location);
        JSONObject now = this.decompressStringToJson(
                rest.getForObject(
                        "https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + key, byte[].class
                )
        );
        if(now == null) return null;
        vo.setNow(now.getJSONObject("now"));
        JSONObject hour = this.decompressStringToJson(
                rest.getForObject(
                        "https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key, byte[].class
                )
        );
        if(hour == null) return null;
        vo.setHourly(new JSONArray(hour.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }

   /**
    * @description: 解压gzip文件，转化成为Json
    * @param: [bytes]
    * @return: com.alibaba.fastjson2.JSONObject
    * @author Ll
    * @date: 2024/7/18 下午2:32
    */
    private JSONObject decompressStringToJson(byte[] bytes){
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gis.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            gis.close();
            output.close();
            return JSONObject.parseObject(output.toString());
        } catch (IOException e) {
            return null;
        }
    }

}
