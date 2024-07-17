package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.PrivacySaveVO;
import com.example.mapper.AccountPrivacyMapper;
import com.example.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Ll
 * @description: 用户隐私功能的具体实现
 * @date 2024/7/17 上午11:24
 */
@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    /**
     * @description: 选择需要展示的信息
     * @param: [id, vo]
     * @return: void
     * @author Ll
     * @date: 2024/7/17 下午1:18
     */
    @Override
    @Transactional
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacy accountPrivacy = this.getPrivacy(id);
        boolean status = vo.isStatue();
        switch (vo.getType()){
            case "phone": accountPrivacy.setPhone(status);break;
            case "email": accountPrivacy.setEmail(status);break;
            case "wx": accountPrivacy.setWx(status);break;
            case "qq": accountPrivacy.setQq(status);break;
            case "gender": accountPrivacy.setGender(status);break;
        }
        this.saveOrUpdate(accountPrivacy);
    }

    @Override
    public AccountPrivacy getPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }

}
