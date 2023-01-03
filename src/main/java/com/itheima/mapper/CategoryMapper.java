package com.itheima.mapper;

import com.itheima.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【category(菜品及套餐分类)】的数据库操作Mapper
* @createDate 2022-11-12 10:22:36
* @Entity com.itheima.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}




