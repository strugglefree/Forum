package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Follow;
import com.example.entity.vo.response.FollowInfoVO;

import java.util.List;

/**
 * @Author: 程凯
 * @Package: com.example.service
 * @Project: Forum
 * @Name: FollowService
 * @Date: 2025/3/7  下午8:41
 */
public interface FollowService extends IService<Follow> {
    int follow(Integer uid, Integer followUid);
    int cancelFollow(Integer uid, Integer followUid);
    List<Integer> getFollowList(int uid);
    List<FollowInfoVO> getFollowInfo(int uid);
}
