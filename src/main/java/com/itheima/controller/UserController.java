package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.R;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;


    /**
     * 获取验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (phone != null) {
            //生成随机的四位验证码
            String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));
            log.info("code={}", code);

            //调用阿里云的短信服务API发送短信,因为搞不定阿里云的短信服务，就不发送短信了，直接在控制台看验证码
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将验证码存入Session
            //session.setAttribute(phone, code);

            //将验证码存入redis中，并设置验证码过期时间
            redisTemplate.opsForValue().setIfAbsent(phone,code,5, TimeUnit.MINUTES);

            return R.success("短信验证码发送成功");
        }

        return R.error("短信验证码发送失败");
    }


    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中取出生成的验证码
        //Object codeInSession = session.getAttribute(phone);

        //从redis中取出生成的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //比对验证码是否相同
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果能比对成功，证明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(phone != null, User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //根据手机号判断是否为新用户，若是新用户则自动注册
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            //登录成功后，删除redis中的数据
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 登出功能
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request) {
        //释放session
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}
