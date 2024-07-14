package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ll
 * @description: 接口
 * @date 2024/7/13 上午10:36
 */
@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    private AccountService service;

    @GetMapping("/ask-code")
    public RestBean<Void> askCode(@RequestParam @Email String email,
                                  @RequestParam @Pattern(regexp = "(register|reset)") String type,
                                  HttpServletRequest request) {
       return this.messageHandle(() -> service.registerEmailVerifyCode(type,email,request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return this.messageHandle(vo , service::registerEmailAccount);
    }

    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        return this.messageHandle(vo , service::resetConfirm);
    }

    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO vo){
        return this.messageHandle(vo , service::resetAccountEmailPassword);
    }

    /**
     * @description: 针对于只有一个对象参数的接口再次优化
     * @param: [vo, function]
     * @return: com.example.entity.RestBean<java.lang.Void>
     * @author Ll
     * @date: 2024/7/14 上午9:43
     */
    private <T> RestBean<Void> messageHandle(T vo , Function<T, String> function){
        return this.messageHandle(() -> function.apply(vo));
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
