package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.entity.vo.request.PrivacySaveVO;
import com.example.entity.vo.response.AccountDetailsVO;
import com.example.entity.vo.response.AccountPrivacyVO;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    @Resource
    private AccountPrivacyService privacyService;
    @Resource
    ControllerUtils utils;

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
        return utils.messageHandle(() -> service.modifyEmail(id,vo));
    }

    @PostMapping("/change-password")
    public RestBean<Void> resetPassword(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                        @RequestBody @Valid ChangePasswordVO vo){
        return utils.messageHandle(() -> service.changePassword(id,vo));
    }

    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid PrivacySaveVO vo){
        privacyService.savePrivacy(id, vo);
        return RestBean.success();
    }

    @GetMapping("/privacy")
    public RestBean<AccountPrivacyVO> privacy(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(privacyService.getPrivacy(id).asViewObject(AccountPrivacyVO.class));
    }

}
