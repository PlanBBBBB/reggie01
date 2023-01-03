package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.ShoppingCart;
import com.itheima.service.ShoppingCartService;
import com.itheima.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

/**
* @author 86139
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-11-20 17:19:12
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{

}




