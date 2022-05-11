package com.csu.mall.service;

import com.csu.mall.common.Result;
import com.csu.mall.pojo.User;
import com.csu.mall.util.Page4Navigator;

public interface UserService {
    //用户登录
    Result<User> login(String username, String password);
    //注册时检查字段是否可用
    Result<String> checkField(String fieldName, String fieldValue);
    //用户注册
    Result<String> register(User user);
    //获取忘记密码的问题
    Result<String> getForgetQuestion(String username);
    //校验忘记密码的问题和答案是否正确
    Result<String> checkForgetAnswer(String username, String question,String answer);
    //通过忘记密码的问题答案重置密码
    Result<String> resetForgetPassword(String username, String newPassword, String forgetToken);
    //登录状态下重置密码
    Result<String> resetPassword(String oldPassword, String newPassword, User user);
    //登录状态下更新用户信息
    Result<String> updateUserInfo(User user);
    //登录状态下获取用户详细信息
    Result<User> getUserDetail(Integer userId);
    //返回分页对象及其数据
    Page4Navigator<User> pageBreak(int start,int size,int navigatePages);
    //根据姓名返回用户对象
    User getByName(String name);
}
