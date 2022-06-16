package com.csu.mall.service.impl;

import com.csu.mall.persistence.OrderItemRepository;
import com.csu.mall.pojo.Order;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.User;
import com.csu.mall.service.OrderItemService;
import com.csu.mall.service.OrderService;
import com.csu.mall.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//添加Redis缓存，并由对应键来控制该类
@CacheConfig(cacheNames = "orderItems")
public class OrderItemServiceImpl implements OrderItemService {
    //注入OrderItem数据
    //逻辑通过OrderItemsDAO取出数据再放在Order的OrderItem上
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ProductImageService productImageService;

    @Override
    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    @Override
    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi :orderItems) {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();
            totalNumber+=oi.getNumber();
            productImageService.setFirstProdutImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
    }

    @Override
    @Cacheable(key="'orderItems-oid-'+ #p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemRepository.findByOrderOrderByIdDesc(order);
    }

    @Override
    @Cacheable(key="'orderItems-pid-'+ #p0.id")
    public List<OrderItem> listByProduct(Product product) {
        return orderItemRepository.findByProduct(product);
    }

    @Override
    public int getSaleCount(Product product) {
        List<OrderItem> list = orderItemRepository.findByProduct(product);
        int result = 0;
        //订单非空判断
        for(OrderItem orderItem:list){
            if (null!=orderItem){
                if(null!= orderItem.getOrder() && null!=orderItem.getOrder().getPayDate()){
                    result +=orderItem.getNumber(); //统计购买数量
                }
            }
        }

        return result;
    }

    @Override
    @Cacheable(key="'orderItems-uid-'+ #p0.id")
    public List<OrderItem> listByUser(User user) {
        return orderItemRepository.findByUserAndOrderIsNull(user);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void update(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void add(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    @Override
    @Cacheable(key = "'orderItems-one'+#p0")
    public OrderItem getById(int id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteById(int id) {
        orderItemRepository.deleteById(id);
    }
}
