package com.csu.mall.persistence;

import com.csu.mall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    //用于注册时检验用户是否存在
    User findByName(String name);
    //登录时通过账号和密码获取用户
    User getByNameAndPassword(String name, String password);
    //通过电话号码获取用户
    User findByPhone(String phone);
    //通过邮箱获取用户
    User findByEmail(String phone);
    //通过用户名和问题和答案来获取用户
    User findByNameAndQuestionAndAnswer(String name, String question, String answer);
}
