package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Interact;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.*;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import com.example.utils.IPUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int type,
                                                    @RequestParam @Min(0) int page){
        return RestBean.success(topicService.getListTopic(page+1,type));
    }

    @GetMapping("/get-ip")
    public RestBean<String> getIp(HttpServletRequest request) {
        return RestBean.success(ipUtils.getIpAddr(request));
    }

    @GetMapping("/top-topic")
    public RestBean<List<TopTopicVO>> topTopic(){
        return RestBean.success(topicService.getListTopTopic());
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailsVO> topic(@RequestParam @Min(0) int tid,
                                          @RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.getTopicDetails(tid,uid));
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = ("like|collect")) String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int id){
        topicService.interact(new Interact(tid,id,new Date(),type),state);
        return RestBean.success();
    }

    @GetMapping("/collect")
    public RestBean<List<TopicPreviewVO>> collect(@RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(topicService.getCollectionTopic(uid));
    }

    @PostMapping("/topic-update")
    public RestBean<Void> updateTopic(@RequestBody @Valid TopicUpdateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int uid){
        return controllerUtils.messageHandle(() -> topicService.updateTopic(vo, uid));
    }


}
