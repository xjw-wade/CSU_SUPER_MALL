package com.csu.mall.persistence;

import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    //返回某产品对应的评价集合
    List<Review> findByProductOrderByIdDesc(Product product);
    //返回某产品对应的评价数量
    int countByProduct(Product product);
}
