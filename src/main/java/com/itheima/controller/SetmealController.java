package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.dto.SetmealDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    DishService dishService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        //精确清理当前分类的缓存
        String key = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增套餐成功");
    }

    /**
     * 分页展示套餐功能
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = new ArrayList<>();

        for (Setmeal record : records) {

            SetmealDto setmealDto = new SetmealDto();
            //得到分类id
            Long categoryId = record.getCategoryId();
            //根据id得到对应的分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //根据该对象得到分类的名称
                String categoryName = category.getName();
                //将分类名称存入dishDto对象中
                setmealDto.setCategoryName(categoryName);
            }
            //将其余属性拷贝到该对象中
            BeanUtils.copyProperties(record, setmealDto);

            list.add(setmealDto);
        }

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }


    /**
     * 更改套餐起售、停售状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable("status") Integer status, Long[] ids) {
        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);
            if (setmeal != null) {
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("套餐售卖状态修改成功");
    }

    /**
     * 修改套餐回显功能
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        if (setmealDto != null) {
            return R.success(setmealDto);
        }
        return R.error("没有查询到套餐信息");
    }

    /**
     * 修改套餐功能
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        //精确清理当前分类的缓存
        String key = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("套餐信息修改成功");
    }


    /**
     * 批量删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids) {
        setmealService.removeByIdWithDish(ids);
        return R.success("删除套餐成功");
    }


    /**
     * 根据条件查询套餐数据
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        //动态获取当前分类的key
        String key = "setmeal_" + setmeal.getCategoryId() + "_1";
        ;

        //判断缓存是否存在
        List<Setmeal> list;
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        //如果缓存存在，则直接返回
        if (list != null) {
            return R.success(list);
        }
        //如果缓存不存在，则查询数据库，并将查询到的集合添加到缓存中
        list = new ArrayList<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        list = setmealService.list(queryWrapper);

        //将查询到的集合添加到缓存中
        redisTemplate.opsForValue().setIfAbsent(key, list, 60, TimeUnit.MINUTES);

        return R.success(list);
    }


    /**
     * 点击套餐图片显示套餐里面的菜品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable("id") Long id) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        //获取该套餐下的所有菜品
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDto> dishDtos = new ArrayList<>();

        for (SetmealDish setmealDish : list) {
            DishDto dishDto = new DishDto();
            String dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);

            dishDtos.add(dishDto);
        }

        return R.success(dishDtos);
    }
}
