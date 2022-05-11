package com.csu.mall.service.impl;

import com.csu.mall.common.CONSTANT;
import com.csu.mall.common.Result;
import com.csu.mall.persistence.UserRepository;
import com.csu.mall.pojo.User;
import com.csu.mall.service.UserService;
import com.csu.mall.util.MD5Util;
import com.csu.mall.util.Page4Navigator;
import com.csu.mall.util.TokenCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service("userService")
@CacheConfig(cacheNames="users")
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public Result<User> login(String username, String password){
        String md5Password = MD5Util.md5Encrypt32Upper(password);
        User loginUser = userRepository.getByNameAndPassword(username,md5Password);
        if(loginUser == null){
            return Result.createForError("用户名或密码错误");
        }
        loginUser.setPassword(StringUtils.EMPTY);
        return Result.createForSuccess(loginUser);
    }

    @Override
    public Result<String> checkField(String fieldName, String fieldValue) {
        if(CONSTANT.USER_FIELDS.USERNAME.equals(fieldName)){
            User user = userRepository.findByName(fieldValue);
            if(user != null){
                return Result.createForError("用户名已存在");
            }
        }
        else if(CONSTANT.USER_FIELDS.PHONE.equals(fieldName)){
            User user = userRepository.findByPhone(fieldValue);
            if(user != null){
                return Result.createForError("电话号码已存在");
            }
        }
        else if(CONSTANT.USER_FIELDS.EMAIL.equals(fieldName)){
            User user = userRepository.findByEmail(fieldValue);
            if(user != null){
                return Result.createForError("邮箱已存在");
            }
        }
        else{
            return Result.createForError("参数错误");
        }
        return Result.createForSuccessMessage("参数校验通过");
    }

    @Override
    public Result<String> register(User user) {
        Result<String> checkResult = checkField(CONSTANT.USER_FIELDS.USERNAME, user.getName());
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        checkResult = checkField(CONSTANT.USER_FIELDS.EMAIL, user.getEmail());
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        checkResult = checkField(CONSTANT.USER_FIELDS.PHONE, user.getPhone());
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        user.setPassword(MD5Util.md5Encrypt32Upper(user.getPassword()));
        user.setRole(CONSTANT.ROLE.CUSTOMER);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        try{
            userRepository.save(user);
            return Result.createForSuccessMessage("注册用户成功");
        }catch (Exception e){
            return Result.createForError("注册用户失败");
        }
    }

    @Override
    public Result<String> getForgetQuestion(String username) {
        Result<String> checkResult = this.checkField(CONSTANT.USER_FIELDS.USERNAME,username);
        if(checkResult.isSuccess()){
            return Result.createForError("该用户名不存在");
        }
        String question = userRepository.findByName(username).getQuestion();
        if(StringUtils.isNotBlank(question)){
            return Result.createForSuccess(question);
        }
        return Result.createForError("密码问题为空");
    }

    @Override
    public Result<String> checkForgetAnswer(String username, String question, String answer) {
        User user = userRepository.findByNameAndQuestionAndAnswer(username, question, answer);
        if(user != null){
            String forgetToken = UUID.randomUUID().toString();
            TokenCacheUtil.setToken(username, forgetToken);
            System.out.println(username+":"+forgetToken);
            return Result.createForSuccess(forgetToken);
        }
        return Result.createForError("找回密码的问题答案错误");
    }

    @Override
    public Result<String> resetForgetPassword(String username, String newPassword, String forgetToken) {
        Result<String> checkResult = this.checkField(CONSTANT.USER_FIELDS.USERNAME,username);
        if(checkResult.isSuccess()){
            return Result.createForError("用户名不存在");
        }
        String token = TokenCacheUtil.getToken(username);
        if(StringUtils.isBlank(token)){
            return Result.createForError("token无效或已过期");
        }
        if(StringUtils.equals(token, forgetToken)){
            String md5Password = MD5Util.md5Encrypt32Upper(newPassword);
            User user = userRepository.findByName(username);
            user.setPassword(md5Password);
            try{
                userRepository.save(user);
                return Result.createForSuccessMessage("通过忘记密码问题答案，重置密码成功");
            }catch (Exception e){
                return Result.createForError("通过忘记密码问题答案，重置密码失败,请重新获取token");
            }
        }else{
            return Result.createForError("token错误，请重新获取token");
        }
    }

    @Override
    public Result<String> resetPassword(String oldPassword, String newPassword, User user) {
        User user1 = userRepository.findByName(user.getName());
        if (!MD5Util.md5Encrypt32Upper(oldPassword).equals(user1.getPassword())) {
            return Result.createForError("旧密码错误");
        }
        user1.setPassword(MD5Util.md5Encrypt32Upper(newPassword));
        try{
            userRepository.save(user1);
            return Result.createForSuccessMessage("密码更新成功");
        }catch (Exception e){
            return Result.createForError("密码更新失败");
        }
    }

    @Override
    public Result<String> updateUserInfo(User user) {
        //检查更新的email是否可用
        Result<String> checkResult = checkField(CONSTANT.USER_FIELDS.EMAIL, user.getEmail());
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        //检查更新的phone是否可用
        checkResult = checkField(CONSTANT.USER_FIELDS.PHONE, user.getPhone());
        if(!checkResult.isSuccess()){
            return checkResult;
        }
        User user1 = userRepository.findByName(user.getName());
        user1.setUpdateTime(LocalDateTime.now());
        user1.setEmail(user.getEmail());
        user1.setPhone(user.getPhone());
        user1.setQuestion(user.getQuestion());
        user1.setAnswer(user.getAnswer());

        try{
            userRepository.save(user1);
            return Result.createForSuccessMessage("更新用户信息成功");
        }catch (Exception e){
            return Result.createForError("更新用户信息失败");
        }
    }

    @Override
    public Result<User> getUserDetail(Integer userId) {
        User user = getById(userId);
        if(user == null){
            return Result.createForError("找不到当前用户信息");
        }
        user.setPassword(StringUtils.EMPTY);
        return Result.createForSuccess(user);
    }


    //返回分页对象及其数据
    @Cacheable(key="'users-page-'+#p0+ '-' + #p1")
    @Override
    public Page4Navigator<User> pageBreak(int start,int size,int navigatePages){
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(start, size,sort);
        Page pageFromJPA =userRepository.findAll(pageable);
        return new Page4Navigator<User>(pageFromJPA,navigatePages);
    }



    @Cacheable(key="'users-one-name-'+ #p0")
    public User getByName(String name) {
        System.out.println(name);
        return userRepository.findByName(name);
    }

    @Cacheable(key="'users-one-id-'+ #p0")
    public User getById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    //添加用户
    @CacheEvict(allEntries=true)
    public void add(User user){
        userRepository.save(user);
    }

}
