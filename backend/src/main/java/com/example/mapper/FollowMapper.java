package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Follow;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: 程凯
 * @Package: com.example.mapper
 * @Project: Forum
 * @Name: FollowMapper
 * @Date: 2025/3/7  下午8:45
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {
}
