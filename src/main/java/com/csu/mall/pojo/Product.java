package com.csu.mall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
//告诉es如何匹配此类
@Document(indexName = "csu_super_mall")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @ManyToOne
    @JoinColumn(name="cid")
    private Category category;

    //如果既没有指明 关联到哪个Column,又没有明确要用@Transient忽略，那么就会自动关联到表对应的同名字段
    private String name; //产品名称
    private String subTitle; //小标题
    private float originalPrice;//原始价格
    private float promotePrice;//优惠价格
    private int stock;//库存
    private Date createDate;//创建日期

    //产品图片对象，因为可能一个产品包含多个图片对象，所以这里接收的是序列化的图片对象
    //该字段非数据库所拥有
    @Transient
    private ProductImage firstProductImage;


    //前端页面显示属性
    @Transient
    private List<ProductImage> productSingleImages; //单个产品图片集合
    @Transient
    private List<ProductImage> productDetailImages; //详情产品图片集合
    @Transient
    private int reviewCount; //销量
    @Transient
    private int saleCount; //累计评价

    @Override
    public String toString() {
        return "Product [id=" + id + ", category=" + category + ", name=" + name + ", subTitle=" + subTitle
                + ", originalPrice=" + originalPrice + ", promotePrice=" + promotePrice + ", stock=" + stock
                + ", createDate=" + createDate + ", firstProductImage=" + firstProductImage + ", reviewCount="
                + reviewCount + ", saleCount=" + saleCount + "]";
    }
}
