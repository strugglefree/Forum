package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Ll
 * @description: 贴子展示
 * @date 2024/7/20 下午1:10
 */
@Data
public class TopicPreviewVO {
    int id;
    int type;
    String title;
    String content;
    List<String> image;
    Date time;
    Integer uid;
    String username;
    String avatar;
}
