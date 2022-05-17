package com.csu.mall.controller;


import com.csu.mall.common.Result;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.PropertyValue;
import com.csu.mall.service.ProductService;
import com.csu.mall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {

    @Autowired
    ProductService productService;
    @Autowired
    PropertyValueService propertyValueService;


    //读出属性列表和对应分类属性下对应产品的属性值
    //逻辑上先用分类获得属性，然后再用分类属性的Id和产品id获得对应的属性值
    @GetMapping("/products/{pid}/propertyValues")
    public Result<List<PropertyValue>> list(@PathVariable("pid")int pid) throws Exception{
        Product product = productService.getById(pid);
        //初始化数据
        propertyValueService.init(product);
        //获得数据列表
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return Result.createForSuccess(propertyValues);
    }

    //更新数据
    @PutMapping("/propertyValues")
    public Result<PropertyValue> update(@RequestBody PropertyValue bean) throws Exception {
        propertyValueService.update(bean);
        return Result.createForSuccess("更新成功",bean);
    }



}
