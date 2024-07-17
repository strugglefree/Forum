package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.AccountPrivacy;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 用户隐私设置mapper
 * @date 2024/7/17 上午11:27
 */
@Mapper
public interface AccountPrivacyMapper extends BaseMapper<AccountPrivacy> {
}
