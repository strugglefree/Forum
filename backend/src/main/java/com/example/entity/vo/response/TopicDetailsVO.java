package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 帖子详情页
 * @date 2024/7/21 下午2:19
 */
@Data
public class TopicDetailsVO {
    private Integer id;
    private String title;
    private String content;
    private Integer type;
    private Date time;
    User user;

    @Data
    public static class User{
        private Integer id;
        private String username;
        private String avatar;
        private String intro;
        private Integer gender;
        private String qq;
        private String wx;
        private String email;
        private String phone;
    }
}
