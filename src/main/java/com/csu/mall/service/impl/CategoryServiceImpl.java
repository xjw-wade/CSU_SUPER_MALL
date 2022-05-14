package com.csu.mall.service.impl;

import com.csu.mall.persistence.CategoryRepository;
import com.csu.mall.pojo.Category;
import com.csu.mall.service.CategoryService;
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

//JPA:M_Service
@Service //表明该类是一个服务类
//指明当前类的缓存 归 Redis上 key = categories的管理
@CacheConfig(cacheNames="categories")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;


    //sort对象来着domain层，通常就是用于放置这个系统中，与数据库中的表，一一对应起来的JavaBean的
    //首先创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询,返回list。
    @Override
    @Cacheable(key="'categories-all'")
    public List<Category> categoryList() throws Exception{
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        return categoryRepository.findAll(sort);
    }

    //分页处理 参数用于定制分页对象，第一个参数为开始页，第二个参数为数据最大长度，第三个参数要显示的超链数
    //写入redis缓存
    @Cacheable(key = "'categories-page-'+#p0+'-'+#p1")
    @Override
    public Page4Navigator<Category> pageList(int start, int size, int navigatePages) {
        //要创建JPA的Page对象，首先要通过new PageRequest去创建Pageable，并传入参数
        //PageRequest(int page, int size, Sort sort)
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(start,size,sort);
        //最后才通过Pageable对象创建分页对象
        Page<Category> pageFromJPA = categoryRepository.findAll(pageable);
        //public Page4Navigator(Page<T> pageFromJPA,int navigatePages)
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //图片上传以及信息添加
    @CacheEvict(allEntries = true)
    @Override
    public void addCategory(Category bean) {
        categoryRepository.save(bean);
    }

    //信息删除
    @CacheEvict(allEntries = true)
    @Override
    public void deleteCategory(int id) {
        try {
            categoryRepository.deleteById(id);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    //获取单个数据
    //表明当前返回值存储到redis的对应key中。
    //(默认redis里没值，就会通过JPA去查询值下次再取时就回到redis上去对应的key-value）
    //参数说明：其中p0为第一个参数
    @Cacheable(key = "'category-one-'+#p0")
    @Override
    public Category getCategory(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        return  category;
    }

    //更新某个数据，为了实时更新数据当操作为添加、删除、更新时，则移除当前的redis上的缓存，并
    // 从JPA中重新读取数据再写入到缓存中
    @CacheEvict(allEntries = true)
    @Override
    public void updateCategory(Category bean) {
        categoryRepository.save(bean);
    }
}
