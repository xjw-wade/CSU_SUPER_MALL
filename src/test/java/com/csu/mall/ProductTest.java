package com.csu.mall;

import com.csu.mall.es.ProductESRepository;
import com.csu.mall.pojo.Product;
import com.csu.mall.service.impl.ProductServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductTest {
    @Autowired
    ProductServiceImpl productService;

    @Autowired
    ProductESRepository productESRepository;

    @Autowired
    ProductESRepository productRepository;

    @Test
    public void add() {
        Product product = new Product();
        product.setName("战神笔记本");
        productService.addProduct(product);
    }

    @Test
    public void get() {
//        System.out.println(productESRepository.findById(1));
        Sort sort  = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(0, 5, sort);
        Page<Product> page = this.productESRepository.findByNameLike("战神", pageable);
        System.out.println(page.getContent());

//        Product product = productService.getById(1);
//        System.out.println(product);

    }

    @Test
    public void getAll() {
//        System.out.println(productESRepository.findById(1));
        Sort sort  = Sort.by(Sort.Direction.DESC,"id");
//        Pageable pageable = PageRequest.of(0, 15, sort);
//        Page<Product> page = this.productRepository.findAll(pageable);
        Iterable<Product> productLists = this.productRepository.findAll(sort);
        for (Iterator<Product> its = productLists.iterator(); its.hasNext(); ) {
            Product s = its.next();
            System.out.println(s);
        }
//        System.out.println(page.getContent());

    }
}
