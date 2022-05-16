package com.csu.mall.service.impl;

import com.csu.mall.persistence.PropertyValueRepository;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Property;
import com.csu.mall.pojo.PropertyValue;
import com.csu.mall.service.PropertyService;
import com.csu.mall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueServiceImpl implements PropertyValueService {
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueRepository propertyValueRepository;
    @Override
    public void init(Product product) {
        //通过分类获得分类属性
        List<Property> properties = propertyService.listByCategory(product.getCategory());
        //通过分类属性的id和产品id获得对应的属性值
        for(Property property:properties){
            //PropertyValue getByPropertyAndProduct(Property property, Product product);
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            if (null==propertyValue){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueRepository.save(propertyValue);
            }
        }

    }

    @Override
    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueRepository.findByProductOrderByIdDesc(product);
    }

    @Override
    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueRepository.getByPropertyAndProduct(property,product);
    }

    @Override
    @CacheEvict(allEntries=true)
    public void update(PropertyValue propertyValue) {
        propertyValueRepository.save(propertyValue);
    }
}
