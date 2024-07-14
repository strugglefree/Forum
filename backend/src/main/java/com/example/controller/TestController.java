package com.example.controller;

import com.example.entity.RestBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ll
 * @description: TODO
 * @date 2024/7/14 上午11:56
 */
@RestController
public class TestController {
    @GetMapping("/test")
    public RestBean<Void> test() {
        return RestBean.success();
    }
}
