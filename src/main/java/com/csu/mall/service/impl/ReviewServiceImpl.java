package com.csu.mall.service.impl;

import com.csu.mall.persistence.ReviewRepository;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Review;
import com.csu.mall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="reviews")
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    @Override
    @Cacheable(key="'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product) {
        return reviewRepository.findByProductOrderByIdDesc(product);
    }

    @Override
    @Cacheable(key="'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product) {
        return reviewRepository.countByProduct(product);
    }

    @Override
    @CacheEvict(allEntries=true)
    public void addReview(Review review) {
        reviewRepository.save(review);
    }
}
