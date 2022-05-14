package com.csu.mall.service;

import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Review;

import java.util.List;

public interface ReviewService {

    //获得指定产品的所有评价
    List<Review> list(Product product);
    //获得指定产品的评价数量
    int getCount(Product product);
    //添加评论
    void addReview(Review review);


}
