package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVO;

/**
 * @author Ll
 * @description: TODO
 * @date 2024/7/16 上午9:27
 */
public interface AccountDetailsService extends IService<AccountDetails> {
    AccountDetails findAccountDetailsById(Integer id);
    boolean saveAccountDetails(Integer id , DetailsSaveVO vo);
}
