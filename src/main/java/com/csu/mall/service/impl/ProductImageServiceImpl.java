package com.csu.mall.service.impl;

import com.csu.mall.persistence.ProductImageRepository;
import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.ProductImage;
import com.csu.mall.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="productImages")
public class ProductImageServiceImpl implements ProductImageService {
    @Autowired
    ProductImageRepository productImageRepository;

    @Override
    @CacheEvict(allEntries=true)
    public void addImage(ProductImage bean) {
        productImageRepository.save(bean);
    }

    @Override
    @Cacheable(key="'productImages-single-pid-'+ #p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageRepository.findByProductAndTypeOrderByIdDesc(product,type_single);
    }

    @Override
    @Cacheable(key="'productImages-detail-pid-'+ #p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageRepository.findByProductAndTypeOrderByIdDesc(product,type_detail);
    }

    @Override
    @Cacheable(key="'productImages-one-'+ #p0")
    public ProductImage getImage(int id) {
        return productImageRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(allEntries=true)
    public void deleteImage(int id) {
        productImageRepository.deleteById(id);
    }

    @Override
    public void setFirstProdutImage(Product product) {
        //取出ProductImage 列表对象 一个Product id对应多个ProductImages对象 ，在乎你传入什么Product
        List<ProductImage> list = listSingleProductImages(product);
        //将取出的ProductImage 对象逐一赋值到对应的Product上
        if (!list.isEmpty()){
            product.setFirstProductImage(list.get(0));
        }else{
            product.setFirstProductImage(new ProductImage());
        }
    }

    @Override
    public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProdutImage(product);
    }

    @Override
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProdutImage(orderItem.getProduct());
        }
    }
}
