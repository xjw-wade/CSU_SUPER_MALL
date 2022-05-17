package com.csu.mall.controller;


import com.csu.mall.common.Result;
import com.csu.mall.pojo.Property;
import com.csu.mall.service.PropertyService;
import com.csu.mall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    //映射属性分类，返回page对象用于分页
    @GetMapping("/categories/{cid}/properties")
    public Result<Page4Navigator<Property>> list(@PathVariable("cid") int cid,
                                                 @RequestParam(value = "start",defaultValue = "0") int start,
                                                 @RequestParam(value = "size",defaultValue = "5") int size)throws Exception{
        start = start<0?0:start;
        Page4Navigator<Property> page = propertyService.list(cid,start,size,5);
        return Result.createForSuccess(page);
    }

    @GetMapping("/properties/{id}")
    public Result<Property> get(@PathVariable("id") int id) throws Exception {
        Property bean=propertyService.getById(id);
        return Result.createForSuccess(bean);
    }


    //当提交的数据是json或对象的形式时，使用RequestBody来接受
    @PostMapping("/properties")
    public Result<Property> add(@RequestBody Property bean) throws Exception {
        System.out.println("bean="+bean);
        propertyService.addProperty(bean);
        return Result.createForSuccess(bean);
    }

    //删除数据
    @DeleteMapping("/properties/{id}")
    public Result<String> delete(@PathVariable("id") int id, HttpServletRequest request)throws Exception{
        propertyService.deleteProperty(id);
        return Result.createForSuccess("删除成功");
    }

    //更新数据,前端中传入一个bean
    @PutMapping("/properties")
    public Result<Property> update(@RequestBody Property bean) throws Exception{
        propertyService.updateProperty(bean);
        return Result.createForSuccess(bean);
    }



}
