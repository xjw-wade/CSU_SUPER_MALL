package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.comparator.*;
import com.csu.mall.pojo.Category;
import com.csu.mall.pojo.Product;
import com.csu.mall.service.CategoryService;
import com.csu.mall.service.ProductImageService;
import com.csu.mall.service.ProductService;
import com.csu.mall.util.ImageUtil;
import com.csu.mall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;

    //分页处理 处理并返回超链数以及相关信息
    /*RequestParam处理的是请求参数，而PathVariable处理的是路径变量,*/
    @GetMapping("/category_page")
    public Result<Page4Navigator<Category>> list(
            @RequestParam(value = "start",defaultValue = "0")int start,
            @RequestParam(value = "szie",defaultValue = "5")int size)throws Exception{
        start=start<0?0:start;
        //规定最大显示超链数5
        Page4Navigator<Category> page = categoryService.pageList(start,size,5);
        return  Result.createForSuccess(page);
    }

    //分类信息以及图片上传
    @PostMapping("/add_category")
    //将formData数据选择性地自动装配到参数上
    public Result<Category> list(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        //从请求中读出post请求的formData数据，自动装配name属性到bean上后保存到数据库中
        categoryService.addCategory(bean);
        //将分类图片不保存在本地数据库上，而是选择保存上项目的中
        //接受上传图片，并保存到 img/category目录下
        //文件名使用新增分类的id
        saveOrUpdateImageFile(bean, image, request);
        return Result.createForSuccess(bean);
    }
    //上传文件，这个有问题 如果上传png他只能保存到缓存当中
    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        File imageFolder= new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,bean.getId()+".jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    //删除信息
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") int id, HttpServletRequest request){
        categoryService.deleteCategory(id);
        //从文件流中删除对应id路径下的文件
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,id+".jpg");
        file.delete();
        return Result.createForSuccess("删除成功");
    }

    //获取单个数据
    @GetMapping("/{id}")
    public Result<Category> get(@PathVariable("id") int id) throws Exception{
        Category bean = categoryService.getCategory(id);
        return Result.createForSuccess(bean);
    }

    //更新某个数据
    @PutMapping("/{id}")
    //发送过来的id参数可自动封装到Category上
    //put的参数要使用request.getParameter获取
    public Result<Category> update(@PathVariable("id") int id, MultipartFile image, HttpServletRequest httpServletRequest) throws IOException {
        Category bean = categoryService.getCategory(id);
        String name = httpServletRequest.getParameter("name");
        bean.setName(name);
        categoryService.updateCategory(bean);
        //并将本地文件进行覆盖
        if(image!=null) {
            saveOrUpdateImageFile(bean, image, httpServletRequest);
        }
        return  Result.createForSuccess(bean);
    }


    //读出Product数据
    @GetMapping("/{cid}/products")
    public Result<Page4Navigator<Product>> list(@PathVariable("cid") int cid,
                                                @RequestParam(value = "start", defaultValue = "0") int start,
                                                @RequestParam(value = "size", defaultValue = "5")int size) throws Exception{
        start=start<0?0:start;
        Page4Navigator<Product> page  = productService.productPage(cid,start,size,5);
        //对ProductImages进行内部赋值
        productImageService.setFirstProdutImages(page.getContent());
        return Result.createForSuccess(page);
    }

    //返回所有产品分类列表
    @GetMapping("/category_list")
    public Result<List<Category>> getCategoryList() throws Exception{
        List<Category> cs= categoryService.categoryList();
        productService.fill(cs);   //1. 通过分类填充产品集合
        productService.fillByRow(cs);
        //将category中的list<Product>中的Product中的category置空，避免重复Json化
        categoryService.removeCategoryFromProduct(cs);
        return Result.createForSuccess(cs);
    }


    //分类页面映射,通过分类找到对应商品
    @GetMapping("/{cid}")
    public Result<Category> category(@PathVariable int cid, String sort) {
        Category category = categoryService.getCategory(cid);
        //通过分类找到对应商品
        //新增一个通过分类查询所有产品的方法
        //List<Product> findByCategoryOrderById(Category category);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        //将category中的list<Product>中的Product中的category置空，避免重复Json化
        categoryService.removeCategoryFromProduct(category);
        //获取Product列表 List<Product>  通过getProducts获取列表
        //sort为空时即不排序
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return Result.createForSuccess(category);
    }




}
