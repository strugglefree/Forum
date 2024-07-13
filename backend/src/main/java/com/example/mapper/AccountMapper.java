package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Ll
 * @description: mapper
 * @date 2024/7/12 上午9:37
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
