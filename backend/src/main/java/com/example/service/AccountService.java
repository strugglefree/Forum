package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.EmailRegisterVO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Ll
 * @description: 账号相关的服务
 * @date 2024/7/12 上午9:40
 */
public interface AccountService extends IService<Account> , UserDetailsService {
    Account findAccountByUsernameOrEmail(String input);
    String registerEmailVerifyCode(String type,String email,String ip);
    String registerEmailAccount(EmailRegisterVO vo);
}