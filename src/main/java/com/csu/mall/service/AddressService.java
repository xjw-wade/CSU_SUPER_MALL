package com.csu.mall.service;

import com.csu.mall.pojo.Address;
import com.csu.mall.pojo.User;

import java.util.List;

public interface AddressService {
    //根据用户查询地址列表
    List<Address> listByUser(User user);
    //更新地址
    void update(Address address);
    //添加地址
    void add(Address address);
    //通过id获取地址
    Address getById(int id);
    //通过id删除地址
    void deleteById(int id);

}
