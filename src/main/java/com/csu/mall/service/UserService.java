package com.csu.mall.service;

import com.csu.mall.persistence.UserRepository;
import com.csu.mall.pojo.User;
import com.csu.mall.utill.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames="users")
public class UserService {
    @Autowired
    UserRepository userRepository;

    //返回分页对象及其数据
    @Cacheable(key="'users-page-'+#p0+ '-' + #p1")
    public Page4Navigator<User> list(int start,int size,int navigatePages){
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(start, size,sort);
        Page pageFromJPA =userRepository.findAll(pageable);
        return new Page4Navigator<User>(pageFromJPA,navigatePages);
    }

    //用于校验用户是否存在
    public boolean isExist(String name){
        User user= getByName(name);
        return null!=user;
    }

    @Cacheable(key="'users-one-name-'+ #p0")
    public User getByName(String name) {
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

    //校验用户
    @Cacheable(key="'users-one-name-'+ #p0 +'-password-'+ #p1")
    public User get(String name, String password){
        return userRepository.getByNameAndPassword(name,password);
    }
}
