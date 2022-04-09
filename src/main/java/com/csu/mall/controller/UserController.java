package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.pojo.User;
import com.csu.mall.service.UserService;
import com.csu.mall.utill.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserService userService;


    @GetMapping("/get_user_detail")
    public Result<User> get_user_detail(@PathVariable Integer id) {
        User userInfo = userService.getById(id);
        return Result.success(userInfo);
    }

    //返回分页对象及其数据
    @GetMapping("/user_list")
    public Result<Page4Navigator<User>> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size){
        start = start<0?0:start;
        Page4Navigator<User>  page = userService.list(start,size,5);
        return Result.success(page);
    }
}
