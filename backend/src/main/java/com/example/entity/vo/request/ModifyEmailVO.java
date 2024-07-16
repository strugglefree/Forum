package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @description: 更改邮件操作的请求类
 * @author Ll
 * @date: 2024/7/16 下午2:27
 */
@Data
public class ModifyEmailVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
}
