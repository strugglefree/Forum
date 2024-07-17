package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.PrivacySaveVO;

/**
 * @author Ll
 * @description: 用户隐私功能接口
 * @date 2024/7/17 上午11:23
 */
public interface AccountPrivacyService extends IService<AccountPrivacy> {
    void savePrivacy(int id, PrivacySaveVO vo);
    AccountPrivacy getPrivacy(int id);
}
