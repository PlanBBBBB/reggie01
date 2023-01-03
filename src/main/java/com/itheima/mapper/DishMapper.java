package com.itheima.mapper;

import com.itheima.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2022-11-12 12:10:20
* @Entity com.itheima.entity.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}




