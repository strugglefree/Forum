package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

/**
 * @author Ll
 * @description: 用户隐私状态类
 * @date 2024/7/17 上午11:16
 */
@Data
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId
    final private Integer id;
    private boolean email = true;
    private boolean phone = true;
    private boolean wx = true;
    private boolean qq = true;
    private boolean gender = true;
}
