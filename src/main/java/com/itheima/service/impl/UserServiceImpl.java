package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import com.itheima.mapper.UserMapper;
import org.springframework.stereotype.Service;


/**
* @author 86139
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2022-11-19 20:31:18
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

}




