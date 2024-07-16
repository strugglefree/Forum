package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.AccountDetails;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: 个人信息mapper
 * @date 2024/7/16 上午9:25
 */
@Mapper
public interface AccountDetailsMapper extends BaseMapper<AccountDetails> {
}
