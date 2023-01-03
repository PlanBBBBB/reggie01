package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.OrderDetail;
import com.itheima.service.OrderDetailService;
import com.itheima.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author 86139
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-11-21 16:46:39
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




