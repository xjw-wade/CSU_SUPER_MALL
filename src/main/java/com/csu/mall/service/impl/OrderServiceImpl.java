package com.csu.mall.service.impl;

import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.User;
import com.csu.mall.service.OrderService;
import com.csu.mall.util.Page4Navigator;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orders")
public class OrderServiceImpl implements OrderService {

    @Override
    public Page4Navigator<Order> orderList(int start, int size, int navigatePages) {
        return null;
    }

    @Override
    public void removeOrderFromOrderItem(List<Order> orders) {

    }

    @Override
    public void removeOrderFromOrderItem(Order order) {

    }

    @Override
    public Order getById(int id) {
        return null;
    }

    @Override
    public void update(Order bean) {

    }

    @Override
    public float sumPrice(Order order, List<OrderItem> ois) {
        return 0;
    }

    @Override
    public void addOrder(Order order) {

    }

    @Override
    public List<Order> listByUserWithoutDelete(User user) {
        return null;
    }

    @Override
    public List<Order> listByUserAndNotDeleted(User user) {
        return null;
    }

    @Override
    public void cacl(Order o) {

    }
}
