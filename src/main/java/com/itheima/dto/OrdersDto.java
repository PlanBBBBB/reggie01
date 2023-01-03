package com.itheima.dto;

import com.itheima.entity.Orders;
import com.itheima.entity.OrderDetail;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
