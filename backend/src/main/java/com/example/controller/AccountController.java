package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.entity.vo.response.AccountDetailsVO;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

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
    @Resource
    private AccountDetailsService detailsService;

    @GetMapping("/info")
    public RestBean<AccountVO> userinfo(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(service.findAccountById(id).asViewObject(AccountVO.class));
    }

    @GetMapping("/details")
    public RestBean<AccountDetailsVO> userDetails(@RequestAttribute(Const.ATTR_USER_ID) int id){
        AccountDetails details = Optional
                .ofNullable(detailsService.findAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVO.class));
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid DetailsSaveVO vo){
        boolean success = detailsService.saveAccountDetails(id, vo);
        return success ? RestBean.success() : RestBean.failure(400,"该用户名已被使用，请重新输入");
    }

    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                        @RequestBody @Valid ModifyEmailVO vo){
        return this.messageHandle(() -> service.modifyEmail(id,vo));
    }

    @PostMapping("/change-password")
    public RestBean<Void> resetPassword(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                        @RequestBody @Valid ChangePasswordVO vo){
        return this.messageHandle(() -> service.changePassword(id,vo));
    }

    /**
     * @description: 针对于返回值为String作为错误信息的方法进行统一处理
     * @param: [action]
     * @return: com.example.entity.RestBean<java.lang.Void>
     * @author Ll
     * @date: 2024/7/14 上午9:28
     */
    private RestBean<Void> messageHandle(Supplier<String> action){
        String s = action.get();
        return s == null ? RestBean.success() : RestBean.failure(400,s);
    }
}
