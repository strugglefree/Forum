package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ll
 * @description: TODO
 * @date 2025/4/11 14:37
 */
@RestController
@RequestMapping("/api/admin/user")
public class AccountAdminController {

    @Resource
    AccountService service;
    @Resource
    AccountDetailsService detailsService;
    @Resource
    AccountPrivacyService privacyService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Value("72")
    private int expire;

    /**
     * @description:  管理员获取用户列表
     * @param: [page, size]
     * @return: com.example.entity.RestBean<com.alibaba.fastjson2.JSONObject>
     * @author Ll
     * @date: 2025/4/11 16:32
     */
    @GetMapping("/list")
    public RestBean<JSONObject> accountList(int page, int size) {
        JSONObject result = new JSONObject();
        List<AccountVO> list = service.page(Page.of(page, size))
                .getRecords()
                .stream()
                .map(account -> account.asViewObject(AccountVO.class))
                .toList();
        result.put("total", service.count());
        result.put("list", list);
        return RestBean.success(result);
    }

    /**
     * @description:  管理员获取用户所有信息
     * @param: [id]
     * @return: com.example.entity.RestBean<com.alibaba.fastjson2.JSONObject>
     * @author Ll
     * @date: 2025/4/11 16:32
     */
    @GetMapping("/detail")
    public RestBean<JSONObject> accountDetail(int id) {
        JSONObject result = new JSONObject();
        result.put("detail", detailsService.findAccountDetailsById(id));
        result.put("privacy", privacyService.getPrivacy(id));
        return RestBean.success(result);
    }

    @PostMapping("/save")
    public RestBean<Void> saveAccount(@RequestBody JSONObject object) {
        int id = object.getInteger("id");
        Account current = service.findAccountById(id);
        Account save = object.toJavaObject(Account.class);
        checkBanned(current, save);
        BeanUtils.copyProperties(save, current, "password", "registerTime");
        service.saveOrUpdate(current);
        AccountDetails details = detailsService.findAccountDetailsById(id);
        AccountDetails saveDetails = object.getJSONObject("detail").toJavaObject(AccountDetails.class);
        BeanUtils.copyProperties(saveDetails, details);
        detailsService.saveOrUpdate(details);
        AccountPrivacy privacy = privacyService.getPrivacy(id);
        AccountPrivacy update = object.getJSONObject("privacy").toJavaObject(AccountPrivacy.class);
        BeanUtils.copyProperties(update, privacy);
        privacyService.saveOrUpdate(privacy);
        return RestBean.success();
    }

    /**
     * @description:  检查用户封禁状态
     * @param: [past, current]
     * @return: void
     * @author Ll
     * @date: 2025/4/11 18:51
     */
    public void checkBanned(Account past, Account current) {
        String key = Const.BANNED_BLOCK + past.getId();
        if(!past.isBanned() && current.isBanned()) {
            stringRedisTemplate.opsForValue().set(key,"true",expire, TimeUnit.HOURS);
        }else if(past.isBanned() && !current.isBanned()) {
            stringRedisTemplate.delete(key);
        }
    }
}
