package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 对数据库中的帖子的相关sql
 * @date 2024/7/19 下午2:21
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {

}
