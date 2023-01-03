package com.itheima.service;

import com.itheima.dto.SetmealDto;
import com.itheima.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2022-11-17 09:59:03
*/
public interface SetmealService extends IService<Setmeal> {

    //新增套餐
    void saveWithDish(SetmealDto setmealDto);

    //回显套餐信息
    SetmealDto getByIdWithDish(Long id);

    //删除套餐
    void removeByIdWithDish(Long[] ids);

    //修改套餐
    void updateWithDish(SetmealDto setmealDto);
}
