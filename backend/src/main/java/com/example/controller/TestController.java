package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ll
 * @description: TODO
 * @date 2024/7/11 下午5:23
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/test1")
    public String test() {
        return "test";
    }
}
