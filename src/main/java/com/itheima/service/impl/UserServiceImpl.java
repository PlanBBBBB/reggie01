package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.R;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import com.itheima.mapper.UserMapper;
import com.itheima.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author 86139
 * @description 针对表【user(用户信息)】的数据库操作Service实现
 * @createDate 2022-11-19 20:31:18
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    @Override
    public R<String> sendMsg(User user) {
        //获取手机号
        String phone = user.getPhone();
        if (phone != null) {
            //生成随机的四位验证码
            String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
            log.info("code={}", code);

            //调用阿里云的短信服务API发送短信,因为搞不定阿里云的短信服务，就不发送短信了，直接在控制台看验证码
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将验证码存入redis中，并设置验证码过期时间
            redisTemplate.opsForValue().setIfAbsent(phone, code, 5, TimeUnit.MINUTES);

            return R.success("短信验证码发送成功");
        }
        return R.error("短信验证码发送失败");
    }

    @Override
    public R<User> login(Map map, HttpSession session) {

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //从redis中取出生成的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //比对验证码是否相同
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果能比对成功，证明登录成功
            User user = userMapper.getUserByPhone(phone);

            if (user == null) {
                //根据手机号判断是否为新用户，若是新用户则自动注册
                user = new User();
                user.setPhone(phone);
                save(user);
            }
            session.setAttribute("user", user.getId());

            //登录成功后，删除redis中的数据
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        //释放session
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}


