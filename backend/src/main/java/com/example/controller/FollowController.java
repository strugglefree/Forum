package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.FollowInfoVO;
import com.example.entity.vo.response.FollowVO;
import com.example.service.FollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 程凯
 * @Package: com.example.controller
 * @Project: Forum
 * @Name: FollowController
 * @Date: 2025/3/7  下午8:36
 */
@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Resource
    private FollowService followService;

    @PostMapping("")
    public RestBean<Integer> follow(@RequestBody FollowVO vo) {
        return RestBean.success(followService.follow(vo.getUid(), vo.getFollowUid()));
    }

    @GetMapping("/getFollowList")
    public RestBean<List<Integer>> followList(@RequestParam Integer uid) {
        return RestBean.success(followService.getFollowList(uid));
    }

    @GetMapping("/getFollowInfo")
    public RestBean<List<FollowInfoVO>> followInfo(@RequestParam Integer uid) {
        return RestBean.success(followService.getFollowInfo(uid));
    }

    @PostMapping("/cancelFollow")
    public RestBean<Integer> cancelFollow(@RequestBody FollowVO vo) {
        return RestBean.success(followService.cancelFollow(vo.getUid(), vo.getFollowUid()));
    }


}
