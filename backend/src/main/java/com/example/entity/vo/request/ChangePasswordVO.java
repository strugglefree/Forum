package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Ll
 * @description: 更改密码操作需要的请求类
 * @date 2024/7/16 下午5:40
 */
@Data
public class ChangePasswordVO {
    @Length(min = 6, max = 16)
    private String past_password;
    @Length(min = 6, max = 16)
    private String new_password;
    @Length(min = 6, max = 16)
    private String check_password;
}
