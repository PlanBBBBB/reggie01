package com.itheima.service;

import com.itheima.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 86139
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-11-21 16:46:34
*/
public interface OrdersService extends IService<Orders> {

    //用户下单功能
    void submit(Orders orders);
}
