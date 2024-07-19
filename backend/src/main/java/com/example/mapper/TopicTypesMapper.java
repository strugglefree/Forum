package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.TopicType;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 从数据库中获取帖子类型
 * @date 2024/7/19 下午1:10
 */
@Mapper
public interface TopicTypesMapper extends BaseMapper<TopicType> {
}
