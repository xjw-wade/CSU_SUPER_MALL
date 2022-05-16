package com.csu.mall.service;

import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Property;
import com.csu.mall.pojo.PropertyValue;

import java.util.List;

public interface PropertyValueService {
    //这个方法的作用是初始化PropertyValue。 为什么要初始化呢？
    // 因为对于PropertyValue的管理，没有增加，只有修改。
    // 所以需要通过初始化来进行自动地增加，以便于后面的修改。
    void init(Product product);
    //通过产品查询propertyValue列表
    List<PropertyValue> list(Product product);
    //通过产品和Property获取PropertyValue
    PropertyValue getByPropertyAndProduct(Product product, Property property);
    //更新数据
    void update(PropertyValue propertyValue);
}
