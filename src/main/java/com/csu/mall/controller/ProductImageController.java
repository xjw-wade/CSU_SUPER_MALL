package com.csu.mall.controller;

import com.csu.mall.common.Result;
import com.csu.mall.pojo.Product;
import com.csu.mall.pojo.ProductImage;
import com.csu.mall.service.ProductImageService;
import com.csu.mall.service.ProductService;
import com.csu.mall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductImageController {

    //获取图片（包含single和detail）
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @GetMapping("/products/{pid}/productImages")
    //  var url =  "products/"+pid+"/"+this.uri+"?type=single";
    public Result<List<ProductImage>> list(@PathVariable("pid")int pid,
                                   @RequestParam("type")String type)throws Exception{
         Product product = productService.getById(pid);
         //判断类型是single还是detail
        if (ProductImageService.type_single.equals(type)){ //single
            List<ProductImage> singles =  productImageService.listSingleProductImages(product);
            return Result.createForSuccess(singles);
        }else if(ProductImageService.type_detail.equals(type)){ //detail
            List<ProductImage> details = productImageService.listDetailProductImages(product);
            return Result.createForSuccess(details);
        }
            return Result.createForSuccess(new ArrayList<>());
    }

    //添加图片
    // productImages?type=single&pid=pid~~~
    //MultipartFile image为post提交的图片信息
    @PostMapping("/productImages")
    public Result<ProductImage> add(@RequestParam("type")String type,
                                    @RequestParam("pid")int pid, MultipartFile image,
                                    HttpServletRequest request)throws Exception{
        //获取新增的ProductImages信息，并添加到数据库中
        ProductImage bean = new ProductImage();
        Product product = productService.getById(pid);
        bean.setProduct(product);
        bean.setType(type);
        productImageService.addImage(bean);

        //将图片文件保存在本地
        String folder = "img/";
        if (ProductImageService.type_single.equals(bean.getType())){
                folder = folder + "productSingle";
        }else{
                folder = folder + "productDetail";
        }

        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }

        return Result.createForSuccess(bean);

    }
    //删除图片
    @DeleteMapping("/productImages/{id}")
    public Result<String> delete(@PathVariable("id") int id, HttpServletRequest request)  throws Exception {
        ProductImage bean = productImageService.getImage(id);
        productImageService.deleteImage(id);

        String folder = "img/";
        if(ProductImageService.type_single.equals(bean.getType()))
            folder +="productSingle";
        else
            folder +="productDetail";

        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        file.delete();
        if(ProductImageService.type_single.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }

        return Result.createForSuccess("删除成功");
    }




}
