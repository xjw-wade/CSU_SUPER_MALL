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


}
