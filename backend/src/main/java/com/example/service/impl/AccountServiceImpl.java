package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.EmailRegisterVO;
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
            template.convertAndSend("mail",data);
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
        String key = Const.VERIFY_EMAIL_DATA + email;
        String username = vo.getUsername();
        String code = stringRedisTemplate.opsForValue().get(key);//Redis存储的验证码
        if(code == null){ return "请先获取验证码"; }
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        if(this.existEmail(email)) return "该邮箱已被注册，请更换一个邮箱";
        if(this.existEmail(username)) return "该用户名已被使用，请重新输入";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null, username, password, email, "user", new Date());
        if(this.save(account)) {
            stringRedisTemplate.delete(key);//删除Redis存储的验证码
            return null;
        }
        else return "内部错误，请联系管理员";
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
