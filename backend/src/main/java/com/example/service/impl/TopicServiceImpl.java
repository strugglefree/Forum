package com.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.mapper.TopicMapper;
import com.example.mapper.TopicTypesMapper;
import com.example.service.TopicService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ll
 * @description: 发布帖子的相关功能的实现
 * @date 2024/7/19 下午1:01
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Resource
    private TopicTypesMapper mapper;
    @Resource
    private FlowUtils flowUtils;

    @PostConstruct
    public void init() {
        type = this.getListTopicTypes().stream().map(TopicType::getId).collect(Collectors.toSet());
    }

    private static Set<Integer> type = null;

    @Override
    public List<TopicType> getListTopicTypes() {
        return mapper.selectList(null);
    }

    @Override
    public String createTopic(TopicCreateVO vo, int uid) {
        if (!this.textLimitCheck(vo.getContent())){
            return "您输入的字符超出字数限制了，请做部分精简";
        }
        if(!type.contains(vo.getType())){ return "非法类型！"; }
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if(!flowUtils.limitPeriodCountCheck(key,10,1800)){
            return "您发文过于频繁，请稍后再试...";
        }
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo,topic);
        topic.setUid(uid);
        topic.setContent(vo.getContent().toString());
        topic.setTime(new Date());
        if(!this.save(topic)){
            return "内部错误，请联系管理员";
        }
        return null;
    }

    /**
     * @description: 检查是否超出字符限制
     * @param: [text]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/19 下午3:10
     */
    private boolean textLimitCheck(JSONObject text){
        if(text == null) return false;
        long length = 0;
        for (Object op : text.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if(length>20000) return false;
        }
        return true;
    }
}
