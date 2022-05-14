package com.csu.mall.service;

import com.csu.mall.pojo.OrderItem;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.ProductImage;

import java.util.List;

public interface ProductImageService {
    //添加图片
    void addImage(ProductImage bean);
    //查找单个图片列表
    List<ProductImage> listSingleProductImages(Product product);
    //查找详细图片列表
    List<ProductImage> listDetailProductImages(Product product);
    //通过id获取图片
    ProductImage getImage(int id);
    //通过id删除图片
    void deleteImage(int id);
    //设置预览图
    void setFirstProdutImage(Product product);
    //为多个Product对象设置预览图
    // （Ps:listProduct页显示多个Product，所以是为多个Product对象在内部使用setter的方式对Pro~img对象赋值）
    void setFirstProdutImages(List<Product> products);
    //为多个OrderItem对象设置预览图
    void setFirstProdutImagesOnOrderItems(List<OrderItem> ois);

}
