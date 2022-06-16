package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.persistence.ProductRepository;
import com.csu.mall.pojo.*;
import com.csu.mall.service.*;
import com.csu.mall.util.Page4Navigator;
import com.csu.mall.util.RedisUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/product")
public class ProductController implements InitializingBean {
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    RedisUtil redisUtil;


    //获取product_list
    @GetMapping("/product_list")
    public Result<Page4Navigator<Product>> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                                @RequestParam(value = "size", defaultValue = "5")int size) throws Exception{
        start=start<0?0:start;
        Page4Navigator<Product> page  = productService.productList(start,size,5);
        //对ProductImages进行内部赋值
        productImageService.setFirstProdutImages(page.getContent());
        return Result.createForSuccess(page);
    }

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

    /**
     * 系统初始化的时候做的事情。
     * 在容器启动时候，检测到了实现了接口InitializingBean之后，
     */
    @Override
    public void afterPropertiesSet() throws Exception {
//        Sort sort = Sort.by(Sort.Direction.DESC, "id");
//        Iterable<Product> productLists = productRepository.findAll(sort);
//        for (Iterator<Product> its = productLists.iterator(); its.hasNext(); ) {
//            Product product = its.next();
//            redisUtil.set(String.valueOf(product.getId()), product.getStock(), 3600);
//        }
    }

}
