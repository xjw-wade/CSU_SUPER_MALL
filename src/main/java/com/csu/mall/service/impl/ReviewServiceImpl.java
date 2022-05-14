package com.csu.mall.service.impl;

import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Review;
import com.csu.mall.service.ReviewService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="reviews")
public class ReviewServiceImpl implements ReviewService {
    @Override
    public List<Review> list(Product product) {
        return null;
    }

    @Override
    public int getCount(Product product) {
        return 0;
    }

    @Override
    public void addReview(Review review) {

    }
}
