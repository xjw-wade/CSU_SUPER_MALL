package com.csu.mall.persistence;

import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Property;
import com.csu.mall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyValueRepository extends JpaRepository<PropertyValue, Integer> {
    //根据产品查询属性值列表
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    //根据产品和属性查询属性值
    PropertyValue getByPropertyAndProduct(Property property, Product product);

}
