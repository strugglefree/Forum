package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Ll
 * @description: 返回的个人信息类
 * @date 2024/7/16 上午10:09
 */
@Data
public class AccountDetailsVO {
    Integer gender;
    String phone;
    String qq;
    String wx;
    String intro;
}
