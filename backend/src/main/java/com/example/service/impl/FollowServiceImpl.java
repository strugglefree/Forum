package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.Follow;
import com.example.entity.vo.response.FollowInfoVO;
import com.example.mapper.AccountMapper;
import com.example.mapper.FollowMapper;
import com.example.service.FollowService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: 程凯
 * @Package: com.example.service.impl
 * @Project: Forum
 * @Name: FollowServiceImpl
 * @Date: 2025/3/7  下午8:44
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService{

    @Resource
    FollowMapper mapper;

    @Resource
    AccountMapper accountMapper;

    @Override
    public int follow(Integer uid, Integer followUid) {
        return mapper.insert(new Follow(uid,followUid));
    }

    @Override
    public int cancelFollow(Integer uid, Integer followUid) {
        return mapper.delete(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUid, uid)
                .eq(Follow::getFollowUid, followUid)
        );
    }

    @Override
    public List<Integer> getFollowList(int uid) {
        return baseMapper.selectObjs(
                new LambdaQueryWrapper<Follow>()
                        .select(Follow::getFollowUid)
                        .eq(Follow::getUid, uid)
        ).stream().map(obj -> (Integer) obj).toList();
    }

    @Override
    public List<FollowInfoVO> getFollowInfo(int uid) {

        List<Integer> followUidList = getFollowList(uid);
        if (followUidList.isEmpty()) {
            return Collections.emptyList();
        }

        // `selectMaps()` 返回 Map<String, Object>，避免类型转换问题
        List<Map<String, Object>> result = accountMapper.selectMaps(
                new LambdaQueryWrapper<Account>()
                        .select(Account::getUsername, Account::getAvatar)
                        .in(Account::getId, followUidList)
        );

        return result.stream()
                .map(map -> new FollowInfoVO(
                        (String) map.get("username"),
                        (String) map.get("avatar")
                ))
                .toList();
    }
}
