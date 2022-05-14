package com.csu.mall.persistence;

import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Integer> {
    //一个订单可能包含多个商品
    List<OrderItem> findByOrderOrderByIdDesc(Order order);

    //通过产品找对应的订单数 一个product主键id对应多个Pid
    List<OrderItem> findByProduct(Product product);

    //基于用户对象user，查询没有生成订单的订单项集合
    List<OrderItem> findByUserAndOrderIsNull(User user);


}
