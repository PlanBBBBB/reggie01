package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.dto.OrdersDto;
import com.itheima.entity.OrderDetail;
import com.itheima.entity.Orders;
import com.itheima.entity.ShoppingCart;
import com.itheima.service.OrderDetailService;
import com.itheima.service.OrdersService;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrdersService ordersService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 下单功能
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }


    //避免在stream中遍历的时候直接使用构造条件来查询导致eq叠加，从而导致后面查询的数据都是null
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }

    /**
     * 移动端分页展示订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> list(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Orders::getOrderTime);
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersService.page(ordersPage, queryWrapper);

        List<Orders> records = ordersPage.getRecords();

        ArrayList<OrdersDto> list = new ArrayList<>();


        for (Orders record : records) {

            OrdersDto ordersDto = new OrdersDto();
            //获取订单id
            Long orderId = record.getId();

            List<OrderDetail> list1 = this.getOrderDetailListByOrderId(orderId);

            BeanUtils.copyProperties(record, ordersDto);
            ordersDto.setOrderDetails(list1);

            list.add(ordersDto);
        }

        ordersDtoPage.setRecords(list);

        return R.success(ordersDtoPage);
    }


    /**
     * 后台订单分页展示
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Orders::getOrderTime)
                .like(number != null, Orders::getId, number)
                .ge(beginTime != null, Orders::getOrderTime, beginTime)
                .le(endTime != null, Orders::getOrderTime, endTime);

        ordersService.page(ordersPage, queryWrapper);
        return R.success(ordersPage);
    }


    /**
     * 后台修改订单状态
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> changeStatus(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return R.success("修改订单状态成功");
    }


    /**
     * 再来一单功能
     *
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String, String> map) {
        //前端页面会直接跳转到购物车页面
        //故需要先将购物车中数据清除

        //根据userId，删除该用户此时所剩的购物车数据(清空购物车)
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        shoppingCartService.remove(queryWrapper);

        //需要将原来购物车数据复制到购物车中
        String id = map.get("id");
        LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> list = orderDetailService.list(queryWrapper1);

        ArrayList<ShoppingCart> shoppingCartsLists = new ArrayList<>();
        for (OrderDetail orderDetail : list) {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setImage(orderDetail.getImage());
            Long dishId = orderDetail.getDishId();
            Long setmealId = orderDetail.getSetmealId();
            if (dishId != null) {
                //如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                //添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartsLists.add(shoppingCart);
        }
        shoppingCartService.saveBatch(shoppingCartsLists);

        return R.success("操作成功");
    }

}
