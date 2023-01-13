package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 新增菜品功能
     *
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        //精确清理当前分类的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询，即显示菜品信息到前端
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = new ArrayList<>();

        for (Dish record : records) {

            DishDto dishDto = new DishDto();
            //得到分类id
            Long categoryId = record.getCategoryId();
            //根据id得到对应的分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //根据该对象得到分类的名称
                String categoryName = category.getName();
                //将分类名称存入dishDto对象中
                dishDto.setCategoryName(categoryName);
            }
            //将其余属性拷贝到该对象中
            BeanUtils.copyProperties(record, dishDto);

            list.add(dishDto);
        }

//        List<DishDto> list = records.stream().map((item) -> {
//            DishDto dishDto = new DishDto();
//
//            BeanUtils.copyProperties(item,dishDto);
//
//            Long categoryId = item.getCategoryId();//分类id
//            //根据id查询分类对象
//            Category category = categoryService.getById(categoryId);
//
//            if(category != null){
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//            return dishDto;
//        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 菜品信息回显功能
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        if (dishDto != null) {
            return R.success(dishDto);
        }
        return R.error("没有查询到菜品信息");
    }

    /**
     * 删除菜品功能（逻辑删除）
     *
     * @param ids
     * @return
     */
    //需修改：若该菜品正处于起售状态不能删除，若该菜品关联了其他套餐不能删除
    @DeleteMapping
    public R<String> delete(Long[] ids) {
        dishService.removeByIdWithFlavor(ids);
        return R.success("删除菜品成功");
    }

    /**
     * 批量起售、停售菜品
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable("status") Integer status, Long[] ids) {
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
                String key = "dish_" + dish.getCategoryId() + "_1";
                redisTemplate.delete(key);
            }
        }
        return R.success("菜品售卖状态修改成功");
    }

    /**
     * 修改菜品功能
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        //精确清理当前分类的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("菜品信息修改成功");
    }


    /**
     * 移动端展示菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //动态获取一个key
        String key = "dish_" + dish.getCategoryId() + "_1";

        //判断缓存是否存在
        List<DishDto> dishDtoList;
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果缓存存在，则直接返回
        if (dishDtoList != null) {
            return R.success(dishDtoList);
        }

        //如果缓存不存在，则查询数据库，并将查询到的集合存入缓存中
        dishDtoList = new ArrayList<>();

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        for (Dish dish1 : list) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            //获得菜品分类的id
            Long categoryId = dish1.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //根据id查询分类对象
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //获取当前菜品的id
            Long dishId = dish1.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> list1 = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(list1);

            dishDtoList.add(dishDto);
        }

        //将查询到的集合存入缓存中
        redisTemplate.opsForValue().setIfAbsent(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
