package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.mapper.TopicMapper;
import com.example.mapper.TopicTypesMapper;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
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
    @Resource
    private CacheUtils cacheUtils;

    @PostConstruct
    public void init() {
        type = this.getListTopicTypes().stream().map(TopicType::getId).collect(Collectors.toSet());
    }

    private static Set<Integer> type = null;

    @Override
    public List<TopicType> getListTopicTypes() {
        return mapper.selectList(null);
    }

    /**
     * @description: 发布帖子
     * @param: [vo, uid]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/20 下午1:50
     */
    @Override
    public String createTopic(TopicCreateVO vo, int uid) {
        if (!this.textLimitCheck(vo.getContent())){
            return "您输入的字符超出字数限制了，请做部分精简";
        }
        if(!type.contains(vo.getType())){ return "非法类型！"; }
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if(flowUtils.limitPeriodCountCheck(key, 10, 1800)){
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
        cacheUtils.deleteFromCache(Const.FORUM_TOPIC_CACHE + "*");
        return null;
    }

    @Override
    public List<TopicPreviewVO> getListTopic(int page, int type) {
        String key = Const.FORUM_TOPIC_CACHE + page + ':' + type;
        List<TopicPreviewVO> list = cacheUtils.getListFromCache(key,TopicPreviewVO.class);
        if(list != null){ return list;}
        List<Topic> topics ;
        if(type == 0) topics=baseMapper.selectTopic(page*10);
        else topics=baseMapper.selectTopicByType(page*10, type);
        if (topics.isEmpty()) { return null;}
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }

    private TopicPreviewVO resolveToPreview(Topic topic){
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic,vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for(Object op : ops){
            Object insert = JSONObject.from(op).get("insert");
            if(insert instanceof String text){
                if(previewText.length() >= 300) continue;
                previewText.append(text);
            }else if(insert instanceof Map<?,?> map){
                Optional.ofNullable(map.get("image")).ifPresent((v) -> images.add(v.toString()));
            }
        }
        vo.setContent(previewText.length() > 300 ? previewText.substring(0,300) : String.valueOf(previewText));
        vo.setImage(images);
        return vo;
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
