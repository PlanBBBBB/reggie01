package com.itheima.mapper;

import com.itheima.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2022-11-21 16:46:34
* @Entity com.itheima.entity.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




