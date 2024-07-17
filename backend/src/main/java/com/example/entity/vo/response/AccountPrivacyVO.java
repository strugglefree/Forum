package com.example.entity.vo.response;

import lombok.Data;

/**
 * @author Ll
 * @description: 响应类：传递需要的数据
 * @date 2024/7/17 下午1:17
 */
@Data
public class AccountPrivacyVO {
    private boolean email;
    private boolean phone;
    private boolean wx;
    private boolean qq;
    private boolean gender;
}
