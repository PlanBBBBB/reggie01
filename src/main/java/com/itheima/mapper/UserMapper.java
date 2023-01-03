package com.itheima.mapper;

import com.itheima.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2022-11-19 20:31:18
* @Entity com.itheima.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




