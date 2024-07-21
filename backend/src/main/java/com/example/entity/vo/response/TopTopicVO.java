package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 置顶帖子
 * @date 2024/7/21 下午1:17
 */
@Data
public class TopTopicVO {
    int id;
    String title;
    Date time;
}
