package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 消息提醒相应类
 * @date 2024/7/25 下午4:05
 */
@Data
public class NotificationVO {
    Integer id;
    String title;
    String content;
    String type;
    String url;
    Date time;
}
