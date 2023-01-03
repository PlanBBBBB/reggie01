package com.itheima.mapper;

import com.itheima.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86139
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2022-11-21 16:46:39
* @Entity com.itheima.entity.OrderDetail
*/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




