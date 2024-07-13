package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return this.messageHandle(() -> service.registerEmailAccount(vo));
    }

    private RestBean<Void> messageHandle(Supplier<String> action){
        String s = action.get();
        return s == null ? RestBean.success() : RestBean.failure(400,s);
    }
}
