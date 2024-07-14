package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 重置密码（对应前端重置密码的第二个页面）
 * @date 2024/7/14 上午8:48
 */

@Data
public class EmailResetVO {
    @Email
    private String email;
    @Length(min = 6 , max = 6)
    private String code;
    @Length(min = 6 , max = 20)
    private String password;
}
