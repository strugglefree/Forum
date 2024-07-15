package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ll
 * @description: 获取账户有关的接口
 * @date 2024/7/15 上午9:18
 */
@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    private AccountService service;

    @GetMapping("/info")
    public RestBean<AccountVO> userinfo(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(service.findAccountById(id).asViewObject(AccountVO.class));
    }
}
