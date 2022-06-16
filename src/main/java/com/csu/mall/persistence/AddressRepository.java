package com.csu.mall.persistence;

import com.csu.mall.pojo.Address;
import com.csu.mall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    //通过用户查询地址列表
    List<Address> findByUser(User user);
}
