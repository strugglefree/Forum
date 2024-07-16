package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author Ll
 * @description: 个人信息相关功能的实现类
 * @date 2024/7/16 上午9:29
 */

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService{

    @Resource
    AccountService service;

    @Override
    public AccountDetails findAccountDetailsById(Integer id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public synchronized  boolean saveAccountDetails(Integer id, DetailsSaveVO vo) {
        Account account = service.findAccountByUsernameOrEmail(vo.getUsername());
        if(account == null || Objects.equals(account.getId(), id)) {
            if(service.update().eq("id",id).set("username",vo.getUsername()).update()){
                AccountDetails accountDetails = new AccountDetails(id, vo.getGender(), vo.getPhone(),vo.getQq(),vo.getWx(),vo.getIntro());
                this.saveOrUpdate(accountDetails);
                return true;
            }
            return false;
        }
        return false;
    }
}
