package com.csu.mall.persistence;

import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    //通过用户和订单状态获取对应的订单列表，但是状态又不是 "delete" 的订单。 "delete" 是作为状态调用的时候传进来的
    //获取到的订单列表用于显示在我的订单中
    List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
