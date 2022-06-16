package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.dto.UpdateAddressDTO;
import com.csu.mall.pojo.Address;
import com.csu.mall.pojo.User;
import com.csu.mall.service.AddressService;
import com.csu.mall.util.CookieUtil;
import com.csu.mall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/address")
public class AddressController {
    @Autowired
    AddressService addressService;
    @Autowired
    RedisUtil redisUtil;

    @GetMapping(value = "/address_list")
    public Result<List<Address>> getByUser(HttpServletRequest request){
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        List<Address> addressList = addressService.listByUser(user);
        return Result.createForSuccess(addressList);
    }

    @PostMapping(value = "/add_address")
    public Result<Address> addAddress(@RequestBody Address address, HttpServletRequest request){
        //读取sessionID
        String loginToken = CookieUtil.readLoginToken(request);
        User user = (User)redisUtil.get(loginToken);
        address.setUser(user);
        addressService.add(address);
        return Result.createForSuccess("添加成功",address);
    }

    @PutMapping(value = "/update_address")
    public Result<Address> updateAddress(@RequestBody Address address, @RequestParam("aid") int aid){
        Address address1 = addressService.getById(aid);
        if (address.getProvince() != null){
            address1.setProvince(address.getProvince());
        }
        if (address.getCity() != null){
            address1.setCity(address.getCity());
        }
        if (address.getDistrict() != null){
            address1.setDistrict(address.getDistrict());
        }
        if (address.getDetail() != null){
            address1.setDetail(address.getDetail());
        }
        if (address.getRecipient() != null){
            address1.setRecipient(address.getRecipient());
        }
        if (address.getPhone() != null){
            address1.setPhone(address.getPhone());
        }
        if (address.getPost() != null){
            address1.setPost(address.getPost());
        }
        addressService.update(address1);
        return Result.createForSuccess(address1);
    }

    @DeleteMapping(value = "/delete_address")
    public Result<String> deleteAddress(@RequestParam("aid") int aid){
        Address address = addressService.getById(aid);
        if (address == null) {
            return Result.createForSuccess("该地址不存在");
        }else{
            addressService.deleteById(aid);
            return Result.createForSuccess("删除成功");
        }

    }







}
