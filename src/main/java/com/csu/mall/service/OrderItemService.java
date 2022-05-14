package com.csu.mall.service;

import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.User;

import java.util.List;

public interface OrderItemService {
    //添加订单项形成订单列表
    void fill(List<Order> orders);
    //添加订单项
    void fill(Order order);
    //返回订单中的订单项列表
    List<OrderItem> listByOrder(Order order);
    //通过商品查询订单项列表
    List<OrderItem> listByProduct(Product product);
    //根据订单的pid 获得产品的销量
    int getSaleCount(Product product);
    //基于用户查询订单项中没有生成订单的订单项
    List<OrderItem> listByUser(User user);
    //更新订单项
    void update(OrderItem orderItem);
    //添加订单项
    void add(OrderItem orderItem);
    //通过id获取订单项
    OrderItem getById(int id);
    //通过id删除订单项
    void deleteById(int id);

}
