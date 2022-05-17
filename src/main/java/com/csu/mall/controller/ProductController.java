package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.ProductImage;
import com.csu.mall.pojo.PropertyValue;
import com.csu.mall.pojo.Review;
import com.csu.mall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;


    //添加Product数据
    //添加数据 接收从前台传过来的json数据
    @PostMapping("/add_product")
    public Result<Product> add(@RequestBody Product bean)throws Exception{
        bean.setCreateDate(new Date());
        productService.addProduct(bean);
        return Result.createForSuccess(bean);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id")int id, HttpServletRequest request)
            throws Exception{
        productService.deleteProduct(id);
        return Result.createForSuccess("删除成功");
    }

    //更新数据
    @PutMapping("/update_product")
    public Result<Product> update(@RequestBody Product bean) throws Exception {
        productService.update(bean);
        return Result.createForSuccess(bean);
    }

    //获取单个数据
    @GetMapping("/products/{id}")
    public Result<Product> get(@PathVariable("id") int id) throws Exception {
        Product bean=productService.getById(id);
        return Result.createForSuccess(bean);
    }




    //产品页映射
    @GetMapping("/{pid}")
    public Result<Map<String, Object>> product(@PathVariable("pid") int pid) {
        //        1. 获取参数pid
//        2. 根据pid获取Product 对象product
//        3. 根据对象product，获取这个产品对应的单个图片集合
//        4. 根据对象product，获取这个产品对应的详情图片集合
//        5. 获取产品的所有属性值
//        6. 获取产品对应的所有的评价
//        7. 设置产品的销量和评价数量
//        8. 把上述取值放在 map 中
//        9. 通过 Result 把这个 map 返回到前端去
        //从数据库中获取值后，赋值给product对象用于返回
        Product product = productService.getById(pid);
        List<ProductImage> images_single = productImageService.listSingleProductImages(product);
        List<ProductImage> images_detail = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(images_single);
        product.setProductDetailImages(images_detail);
        List<PropertyValue> propertyValues = propertyValueService.list(product);//所有属性值
        List<Review> reviews = reviewService.list(product);//对应商品所有评价
        productService.setSaleAndReviewNumber(product); //销量和评价数量
        productImageService.setFirstProdutImage(product);//设置预览图
        //将数据打包封装返回前端
        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", propertyValues);//里面包含property对象
        map.put("reviews", reviews);
        return Result.createForSuccess(map);
    }

    //搜索结果
    @PostMapping("/search")
    public Result<List<Product>> search(@RequestParam("keyword") String keyword, @RequestParam("page") Integer page,
                                        @RequestParam("size") Integer size){
        //从产品列表中取出对应keyword的产品
        if(null==keyword)
            keyword = "";
        if(page == null)
            page = 0;
        if(null==size)
            size = 20;
        //控制搜索结果长度，使用分页Pageable对象和模糊关键字查找对应的产品List
        List<Product> ps= productService.search(keyword,page,size);
        //返回预览图和销量、评价数量用于前端搜索结果页面显示
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return Result.createForSuccess(ps);

    }

}
