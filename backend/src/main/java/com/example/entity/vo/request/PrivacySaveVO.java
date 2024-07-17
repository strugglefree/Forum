package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author Ll
 * @description: 个人隐私设置请求类
 * @date 2024/7/17 上午10:53
 */
@Data
public class PrivacySaveVO {
    @Pattern(regexp = "(phone|email|wx|qq|gender)")
    private String type;
    boolean statue;
}
