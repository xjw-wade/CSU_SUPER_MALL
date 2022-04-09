package com.csu.mall;

import com.csu.mall.pojo.User;
import com.csu.mall.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private UserService service;

    @Test
    public void get() {
        User user = service.getById(1);
        System.out.println(user);
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setName("xjw");
        user.setPassword("123");
        user.setEmail("2810488199@qq.com");
        user.setPhone("18905429465");
        user.setRole(0);
        service.add(user);
        System.out.println("添加用户成功！");
    }


}
