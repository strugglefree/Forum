package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.TopicComment;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author Ll
 * @description: 贴子评论映射器
 * @date 2024/7/24 下午3:37
 */
@Mapper
public interface TopicCommentMapper extends BaseMapper<TopicComment> {
}
