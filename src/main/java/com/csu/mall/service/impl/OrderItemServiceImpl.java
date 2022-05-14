package com.csu.mall.service.impl;

import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.User;
import com.csu.mall.service.OrderItemService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//添加Redis缓存，并由对应键来控制该类
@CacheConfig(cacheNames = "orderItems")
public class OrderItemServiceImpl implements OrderItemService {
    @Override
    public void fill(List<Order> orders) {

    }

    @Override
    public void fill(Order order) {

    }

    @Override
    public List<OrderItem> listByOrder(Order order) {
        return null;
    }

    @Override
    public List<OrderItem> listByProduct(Product product) {
        return null;
    }

    @Override
    public int getSaleCount(Product product) {
        return 0;
    }

    @Override
    public List<OrderItem> listByUser(User user) {
        return null;
    }

    @Override
    public void update(OrderItem orderItem) {

    }

    @Override
    public void add(OrderItem orderItem) {

    }

    @Override
    public OrderItem getById(int id) {
        return null;
    }

    @Override
    public void deleteById(int id) {

    }
}
