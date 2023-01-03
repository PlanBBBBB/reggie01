package com.itheima.service;

import com.itheima.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service
* @createDate 2022-11-12 10:22:36
*/
public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
