package com.csu.mall.service.impl;

import com.csu.mall.persistence.CategoryRepository;
import com.csu.mall.persistence.PropertyRepository;
import com.csu.mall.pojo.Category;
import com.csu.mall.pojo.Property;
import com.csu.mall.service.CategoryService;
import com.csu.mall.service.PropertyService;
import com.csu.mall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="properties")
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    CategoryService categoryService;

    @Override
    @CacheEvict(allEntries=true)
    public void addProperty(Property bean) {
        propertyRepository.save(bean);
    }

    @Override
    @CacheEvict(allEntries=true)
    public void deleteProperty(int id) {
        propertyRepository.deleteById(id);
    }

    @Override
    @Cacheable(key="'properties-one-'+ #p0")
    public Property getById(int id) {
        return propertyRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(allEntries=true)
    public void updateProperty(Property bean) {
        propertyRepository.save(bean);
    }

    @Override
    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.getCategory(cid);
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(start,size,sort);
        //该方法虽然没有实现体，但是按照JPA的规范，依然能找到对应的<Property>并且带分页的page对象
        Page<Property> pageFromJPA =propertyRepository.findByCategory(category,pageable);
        return new Page4Navigator<Property>(pageFromJPA,navigatePages);
    }

    @Override
    @Cacheable(key="'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category) {
        return propertyRepository.findByCategory(category);
    }
}
