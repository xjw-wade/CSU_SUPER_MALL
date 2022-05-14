package com.csu.mall.service.impl;

import com.csu.mall.es.ProductESRepository;
import com.csu.mall.persistence.ProductRepository;
import com.csu.mall.pojo.Category;
import com.csu.mall.pojo.Product;
import com.csu.mall.service.*;
import com.csu.mall.util.Page4Navigator;
import com.csu.mall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductRepository productRepository;
    //添加es的支持
    @Autowired
    ProductESRepository productESRepository;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @Override
    public Page4Navigator<Product> productPage(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.getCategory(cid);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(start, size, sort);
        Page<Product> pageFromJPA =productRepository.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    @Override
    public void addProduct(Product bean) {
        try {
            productRepository.save(bean);
            //添加es支持，即除了dao的支持外还需要将数据通过ElasticsearchRepository同步到es上
            productESRepository.save(bean);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        try {
            productRepository.deleteById(id);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void update(Product product) {
        try {
            productRepository.save(product);
        }catch(Exception e){
            System.out.println(e);
        }

    }

    @Override
    public Product getById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }

    @Override
    public void fill(Category category) {
        //springboot 的缓存机制是通过切面编程 aop来实现的。
        //从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
        //所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存。
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    @Override
    public List<Product> listByCategory(Category category) {
        return productRepository.findByCategoryOrderById(category);
    }

    @Override
    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {//将List<Product> 进行分拆装入List<List<Product>>
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    @Override
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }

    @Override
    public void setSaleAndReviewNumber(List<Product> products) {

    }

    @Override
    public List<Product> search(String keyword, int start, int size) {
        return null;
    }

    @Override
    public void initDatabase2ES(int start, int size) {

    }
}
