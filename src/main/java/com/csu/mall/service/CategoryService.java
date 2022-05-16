package com.csu.mall.service;

import com.csu.mall.pojo.Category;
import com.csu.mall.pojo.Product;
import com.csu.mall.util.Page4Navigator;

import java.util.List;

public interface CategoryService {
    //查询商品类别列表
    List<Category>  categoryList() throws Exception;
    //查询商品类别分页
    Page4Navigator<Category> pageList(int start, int size, int navigatePages);
    //添加商品类别
    void addCategory(Category bean);
    //删除类别
    void deleteCategory(int id);
    //获取商品类别
    Category getCategory(int id);
    //更新商品类别
    void updateCategory(Category bean);
    //将category中的list<Product>中的Product中的category置空，避免重复Json化
    void removeCategoryFromProduct(List<Category> cs);
    //将category中的list<Product>中的Product中的category置空，避免重复Json化
    void removeCategoryFromProduct(Category category);


}
