package com.csu.mall.service;

import com.csu.mall.pojo.Category;
import com.csu.mall.pojo.Property;
import com.csu.mall.util.Page4Navigator;

import java.util.List;

public interface PropertyService {
    //添加属性实体
    void addProperty(Property bean);
    //通过id删除属性实体
    void deleteProperty(int id);
    //通过id获取属性实体
    Property getById(int id);
    //更新属性实体
    void updateProperty(Property bean);
    //返回自定义分页对象
    Page4Navigator<Property> list(int cid, int start, int size, int navigatePages);
    //通过分类获取所有属性集合的方法
    List<Property> listByCategory(Category category);








}
