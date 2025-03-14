package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 用户验证
 * @date 2024/7/11 下午3:27
 */
@Data
public class AuthorizeVO {
    String username;
    String role;
    String token;
    Date expires;
}
