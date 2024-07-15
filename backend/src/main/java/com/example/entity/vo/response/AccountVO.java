package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author Ll
 * @description: 账户信息
 * @date 2024/7/15 上午9:43
 */
@Data
public class AccountVO {
    String username;
    String email;
    String role;
    Date registerTime;
}
