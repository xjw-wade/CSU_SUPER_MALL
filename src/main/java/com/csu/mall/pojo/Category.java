package com.csu.mall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {
    @Id //表明该字段为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) //表明该字段为自增字段
    @Column(name = "id" ) //列名为id
            Integer id;

    @Column(name = "name")
    String name;

    /* ------------------       后端          ------------------      */


    /* ------------------       前端          ------------------      */
    //由于Product对象里面也有Category对象,Category对象下也有Product，为了避免springMVC重复json化的过程
    //只需要把products下的Product对象中的category对象中的Product置为null即可，同理productsByRow
    //一个分类下有多个产品
    //根据业务需求，选择性地将重复迭代的对象置为null
    //category-list<Product>-product-category=null
    //order-list<Orderitem>-Orderitem-(order=null)
    @Transient
    List<Product> products;
    //一个分类又对应多个 List<Product>
    //用于首页竖状导航的分类名称右边显示推荐产品列表
    @Transient
    List<List<Product>> productsByRow;
}
