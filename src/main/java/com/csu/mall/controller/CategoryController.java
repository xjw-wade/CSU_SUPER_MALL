package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.comparator.*;
import com.csu.mall.pojo.Category;
import com.csu.mall.service.CategoryService;
import com.csu.mall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    //返回所有产品分类列表
    @GetMapping("/category_list")
    public Result<List<Category>> getCategoryList() throws Exception{
        List<Category> cs= categoryService.categoryList();
        productService.fill(cs);   //1. 通过分类填充产品集合
        productService.fillByRow(cs);
        //将category中的list<Product>中的Product中的category置空，避免重复Json化
        categoryService.removeCategoryFromProduct(cs);
        return Result.createForSuccess(cs);
    }

    //分类页面映射,通过分类找到对应商品
    @GetMapping("/{cid}")
    public Result<Category> category(@PathVariable int cid, String sort) {
        Category category = categoryService.getCategory(cid);
        //通过分类找到对应商品
        //新增一个通过分类查询所有产品的方法
        //List<Product> findByCategoryOrderById(Category category);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        //将category中的list<Product>中的Product中的category置空，避免重复Json化
        categoryService.removeCategoryFromProduct(category);
        //获取Product列表 List<Product>  通过getProducts获取列表
        //sort为空时即不排序
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return Result.createForSuccess(category);
    }




}
