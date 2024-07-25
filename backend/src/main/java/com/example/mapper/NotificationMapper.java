package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 消息通知映射类
 * @date 2024/7/25 下午3:55
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
