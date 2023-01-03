package com.itheima.mapper;

import com.itheima.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2022-11-20 17:19:12
* @Entity com.itheima.entity.ShoppingCart
*/
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}




