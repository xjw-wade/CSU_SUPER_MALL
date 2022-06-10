package com.csu.mall.service;

import com.csu.mall.pojo.Category;
import com.csu.mall.pojo.Product;
import com.csu.mall.util.Page4Navigator;

import java.util.List;

public interface ProductService {
    //返回产品的分页对象
    Page4Navigator<Product> productList(int start, int size, int navigatePages);
    //获取产品分页数据
    Page4Navigator<Product> productPage(int cid, int start, int size, int navigatePages);
    //添加商品
    void addProduct(Product bean);
    //删除商品
    void deleteProduct(int id);
    //更新商品
    void update(Product product);
    //通过id获取商品
    Product getById(int id);
    /* ------------------       后端          ------------------      */

    /* ------------------       前端          ------------------      */
    //为分类填充产品集合
    void fill(List<Category> categorys);
    void fill(Category category);
    //通过类别查询商品列表
    List<Product> listByCategory(Category category);
    void fillByRow(List<Category> categorys);
    //实现为产品设置销量和评价数量的方法
    void setSaleAndReviewNumber(Product product);
    void setSaleAndReviewNumber(List<Product> products);
    //通过es取出搜索结果
    List<Product> search(String keyword, int start, int size);
    //初始化数据到es上
    void initDatabase2ES(int start, int size);


}
