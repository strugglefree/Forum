package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicPreviewVO;

import java.util.List;

/**
 * @author Ll
 * @description: 帖子相关服务 如获取帖子的所有类型
 * @date 2024/7/19 下午1:00
 */
public interface TopicService extends IService<Topic> {
    List<TopicType> getListTopicTypes();
    String createTopic(TopicCreateVO vo , int uid);
    List<TopicPreviewVO> getListTopic(int page , int type);
}
