package com.itheima.service;

import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2022-11-12 12:10:20
*/
public interface DishService extends IService<Dish> {
    //新增菜品，需要同时操作两张表
    void saveWithFlavor(DishDto dishDto);

    //根据id查询两张表
    DishDto getByIdWithFlavor(Long id);

    //修改菜品，需要同时操作两张表
    void updateWithFlavor(DishDto dishDto);

    //删除菜品，同时操作两张表
    void removeByIdWithFlavor(Long[] ids);

}
