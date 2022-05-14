package com.csu.mall.service;

import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.User;
import com.csu.mall.util.Page4Navigator;

import java.util.List;

public interface OrderService {
    String waitPay = "waitPay";
    String waitDelivery = "waitDelivery";
    String waitConfirm = "waitConfirm";
    String waitReview = "waitReview";
    String finish = "finish";
    String delete = "delete";
    //返回订单的分页对象
    Page4Navigator<Order> orderList(int start, int size, int navigatePages);
    //从列表中取出每个order，获得每个order下的orderItem，再将OrderItem下的Order删除
    void removeOrderFromOrderItem(List<Order> orders);
    //将list中的OrderItem中的每个Order设置为空
    void removeOrderFromOrderItem(Order order);
    //通过id查询订单
    Order getById(int id);
    //更新订单
    void update(Order bean);
    //计算每个订单项的价格
    float sumPrice(Order order, List<OrderItem> ois);
    //添加订单
    void addOrder(Order order);
    //根据用户和订单的对应状态获取商品（状态非delete）
    List<Order>  listByUserWithoutDelete(User user);
    //获取状态非delete的订单list
    List<Order> listByUserAndNotDeleted(User user);
    void cacl(Order o);

}
