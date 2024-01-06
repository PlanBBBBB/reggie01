package com.itheima.service;

import com.itheima.common.R;
import com.itheima.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * @author 86139
 * @description 针对表【user(用户信息)】的数据库操作Service
 * @createDate 2022-11-19 20:31:18
 */
public interface UserService extends IService<User> {

    R<String> sendMsg(User user);

    R<User> login(Map map, HttpSession session);

    R<String> logout(HttpServletRequest request);

}
