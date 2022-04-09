package com.csu.mall.persistence;

import com.csu.mall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    //用于注册时检验用户是否存在
    User findByName(String name);
    //登录时通过账号和密码获取用户
    User getByNameAndPassword(String name, String password);
}
