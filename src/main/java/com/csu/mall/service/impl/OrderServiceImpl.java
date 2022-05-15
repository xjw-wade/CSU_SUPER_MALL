package com.csu.mall.service.impl;

import com.csu.mall.persistence.OrderItemRepository;
import com.csu.mall.persistence.OrderRepository;
import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.User;
import com.csu.mall.service.OrderItemService;
import com.csu.mall.service.OrderService;
import com.csu.mall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orders")
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public Page4Navigator<Order> orderList(int start, int size, int navigatePages) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(start,size,sort);
        Page pageFromJpa = orderRepository.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa,navigatePages);
    }

    @Override
    public void removeOrderFromOrderItem(List<Order> orders) {
        //从列表中取出每个order，获得每个order下的orderItem，再将OrderItem下的Order删除
        for(Order order:orders){
            removeOrderFromOrderItem(order);
        }
    }

    @Override
    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        //将list中的OrderItem中的每个Order设置为空
        for (OrderItem orderItem:orderItems){
            orderItem.setOrder(null);
        }
    }

    @Override
    @Cacheable(key="'orders-one-'+ #p0")
    public Order getById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(allEntries=true)
    public void update(Order bean) {
        orderRepository.save(bean);
    }

    @Override
    @CacheEvict(allEntries=true)
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public float sumPrice(Order order, List<OrderItem> ois) {
        float total = 0;
        //添加订单到数据库
        addOrder(order);
        for (OrderItem oi: ois) {
            oi.setOrder(order);
            orderItemService.update(oi);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        return total;
    }

    @Override
    @CacheEvict(allEntries=true)
    public void addOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserWithoutDelete(User user) {
        List<Order> orders =listByUserAndNotDeleted(user);
        //将订单项的数据填充到对应的订单中，然后返回全部信息= 订单项+订单信息
        orderItemService.fill(orders);
        return orders;
    }

    @Override
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDeleted(User user) {
        //获取状态非delete的订单list
        return orderRepository.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }

    @Override
    public void cacl(Order o) {
        List<OrderItem> orderItems = o.getOrderItems();
        float total = 0;
        for (OrderItem orderItem : orderItems) {
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        o.setTotal(total);
    }
}
