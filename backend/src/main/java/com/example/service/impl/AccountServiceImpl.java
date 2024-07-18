package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.*;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Ll
 * @description: 服务的具体实现
 * @date 2024/7/12 上午9:41
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    AmqpTemplate template;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils flowUtils;

    @Resource
    PasswordEncoder passwordEncoder;

    /**
     * @description:  验证账户信息
     * @param: [username]
     * @return: org.springframework.security.core.userdetails.UserDetails
     * @author Ll
     * @date: 2024/7/13 上午10:15
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByUsernameOrEmail(username);
        if(account == null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * @description:  查找账户信息（Mybatis-plus）
     * @param: [input]
     * @return: com.example.entity.dto.Account
     * @author Ll
     * @date: 2024/7/13 上午9:50
     */
    @Override
    public Account findAccountByUsernameOrEmail(String input) {
        return this.query()
                .eq("username", input)
                .or()
                .eq("email",input)
                .one();
    }

    /**
     * @description: 获取验证码
     * @param: [type, email, ip]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/13 上午9:50
     */
    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()){              //ip锁
            if(!this.VerifyLimit(ip))
                return "请求频繁,请稍后再试！";
            Random random = new Random();
            int code = random.nextInt(89999) + 100000;
            Map<String,Object> data = Map.of("type",type,"email",email,"code",code);
            template.convertAndSend(Const.MQ_MAIL,data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }

    }
    /**
     * @description: 邮件注册
     * @param: [vo]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/13 下午1:43
     */
    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String code = this.getEmailCode(email);
        if(code == null){ return "请先获取验证码"; }
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        if(this.existEmail(email)) return "该邮箱已被注册，请更换一个邮箱";
        if(this.existUsername(username)) return "该用户名已被使用，请重新输入";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null, username, password, email, Const.ROLE_DEFAULT, new Date(),null);
        if(this.save(account)) {
            this.deleteEmailCode(email);//删除Redis存储的验证码
            return null;
        }
        else return "内部错误，请联系管理员";
    }

    /**
     * @description: 重置密码第一步：验证邮箱
     * @param: [vo]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/14 上午8:56
     */
    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        String code = this.getEmailCode(email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    /**
     * @description: 重置密码第二步：重新填写对应的账户的密码
     * @param: [vo]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/14 上午8:56
     */
    @Override
    public String resetAccountEmailPassword(EmailResetVO vo) {
        String email = vo.getEmail();
        String code = vo.getCode();
        String password = passwordEncoder.encode(vo.getPassword());
        String confirm = this.resetConfirm(new ConfirmResetVO(email, code));
        if(confirm != null) return confirm;
        boolean update = this.update().eq("email", email).set("password", password).update();
        if(update) {
            this.deleteEmailCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    /**
     * @description: 更改邮箱验证验证码
     * @param: [id, vo]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/16 下午2:35
     */
    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String email = vo.getEmail();
        String code = this.getEmailCode(email);
        if(code == null){
            return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())){
            return "验证码错误，请重新输入";
        }
        this.deleteEmailCode(email);
        Account account = this.findAccountByUsernameOrEmail(email);
        if(account != null && account.getId() != id){
            return "该邮箱已被别人注册，请更换其他电子邮箱";
        }
        this.update().eq("id", id).set("email", email).update();
        return null;
    }

    /**
     * @description: 更改用户密码
     * @param: [id, vo]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/16 下午6:02
     */
    @Override
    public String changePassword(int id, ChangePasswordVO vo) {
        String password = this.query().eq("id",id).one().getPassword();
        if(!passwordEncoder.matches(vo.getPast_password(), password)){
            return "原密码错误，请核对后重新输入";
        }
        boolean update = this.update()
                .eq("id", id)
                .set("password", this.passwordEncoder.encode(vo.getNew_password()))
                .update();
        return update ? null : "未知错误，请联系管理员";
    }

    /**
     * @description: 根据用户ID查找对应用户
     * @param: [id]
     * @return: com.example.entity.dto.Account
     * @author Ll
     * @date: 2024/7/15 上午9:51
     */
    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    /**
     * @description: 通过ip来限制验证码的获取
     * @param: [ip]
     * @return: boolean
     * @author Ll
     * @date: 2024/7/13 上午9:49
     */
    private boolean VerifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key,60);
    }

    /**
     * @description: 删除邮件验证码
     * @param: [email]
     * @return: void
     * @author Ll
     * @date: 2024/7/14 上午9:17
     */
    private void deleteEmailCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    /**
     * @description: 获取邮件验证码
     * @param: [email]
     * @return: java.lang.String
     * @author Ll
     * @date: 2024/7/14 上午9:12
     */
    private String getEmailCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

/**
 * @description: 邮箱是否已被注册
 * @param: [email]
 * @return: boolean
 * @author Ll
 * @date: 2024/7/13 下午1:52
 */
    private boolean existEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }
/**
 * @description: 用户名是否已被使用
 * @param: [email]
 * @return: boolean
 * @author Ll
 * @date: 2024/7/13 下午1:55
 */
    private boolean existUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username",username));
    }
}
