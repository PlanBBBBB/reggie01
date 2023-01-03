package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.dto.DishDto;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.entity.SetmealDish;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import com.itheima.mapper.DishMapper;
import com.itheima.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 86139
 * @description 针对表【dish(菜品管理)】的数据库操作Service实现
 * @createDate 2022-11-12 12:10:20
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //将菜品信息保存到菜品表中
        this.save(dishDto);

        //将菜品口味存入菜品口味表

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {

        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //将菜品信息更新到菜品表中
        this.updateById(dishDto);

        //先将口味表的数据清空
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //再将菜品口味存入菜品口味表

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.updateBatchById(flavors);
    }

    @Override
    @Transactional
    public void removeByIdWithFlavor(Long[] ids) {

        for (Long id : ids) {
            //需注意：若该菜品正处于起售状态不能删除，若该菜品关联了其他套餐不能删除

            //删除菜品

            //若该菜品正处于起售状态不能删除(抛异常)
            Dish dish = this.getById(id);
            if (dish.getStatus().equals(1)){
                throw new CustomException("存在菜品正处于起售状态不能删除");
            }

            //若该菜品关联了其他套餐不能删除（抛异常）
            List<SetmealDish> list = setmealDishService.list();
            for (SetmealDish setmealDish : list) {
                if (id.equals(setmealDish.getDishId())){
                    throw new CustomException("存在菜品关联了其他套餐不能删除");
                }
            }

            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

            queryWrapper.eq(id != null, Dish::getId, id);

            this.remove(queryWrapper);

            //删除菜品口味
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, id);
            dishFlavorService.remove(queryWrapper1);
        }

    }
}




