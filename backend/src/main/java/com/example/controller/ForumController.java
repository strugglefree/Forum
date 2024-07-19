package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TypesVO;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import com.example.utils.IPUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Resource
    TopicService topicService;
    @Resource
    IPUtils ipUtils;
    @Resource
    ControllerUtils controllerUtils;

    @GetMapping("/weather")
    public RestBean<WeatherVO> getWeather(double longitude, double latitude) {
        WeatherVO vo = weatherService.GetLocationWeather(longitude, latitude);
        return vo == null ?
                RestBean.failure(400,"获取天气信息失败，请联系管理员") : RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<TypesVO>> getTypes() {
        List<TopicType> listTopicTypes = topicService.getListTopicTypes();
        if(listTopicTypes.isEmpty()) {
            return RestBean.failure(400,"内部错误，请联系管理员");
        }
        return RestBean.success(listTopicTypes
                .stream()
                .map(type -> type.asViewObject(TypesVO.class))
                .toList());
    }

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@RequestBody @Valid TopicCreateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int uid){
        return controllerUtils.messageHandle(()->topicService.createTopic(vo, uid));
    }

    @GetMapping("/get-ip")
    public RestBean<String> getIp(HttpServletRequest request) {
        return RestBean.success(ipUtils.getIpAddr(request));
    }

}
