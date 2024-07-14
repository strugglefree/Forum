package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 验证重置密码（对应前端重置密码的第一步骤）
 * @date 2024/7/14 上午8:45
 */

@Data
@AllArgsConstructor
public class ConfirmResetVO {
    @Email
    private String email;
    @Length(min = 6,max = 6)
    private String code;
}
