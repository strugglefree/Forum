package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 展示评论类
 * @date 2024/7/25 下午12:06
 */

@Data
public class CommentVO {
    int id;
    String content;
    Date time;
    String quote;
    User user;

    @Data
    public static class User{
        private Integer id;
        private String username;
        private String avatar;
        private Integer gender;
        private String qq;
        private String wx;
        private String email;
        private String phone;
    }
}
