package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Ll
 * @description: 对数据库中的帖子的相关sql
 * @date 2024/7/19 下午2:21
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
    @Select("""
        select * from db_topic left join db_account on uid = db_account.id
        order by `time` limit ${start} ,10
    """)
    List<Topic> selectTopic(int start);

    @Select("""
        select * from db_topic left join db_account on uid = db_account.id
             where type = #{type}
        order by `time` limit ${start} ,10
    """)
    List<Topic> selectTopicByType(int start, int type);
}
