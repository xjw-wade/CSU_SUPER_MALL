package com.csu.mall.persistence;

import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}
