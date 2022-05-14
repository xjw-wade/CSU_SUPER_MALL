package com.csu.mall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orderitem")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    //一个用户的订单列中可能存在多个商品
    @ManyToOne
    @JoinColumn(name="pid")
    private Product product;

    @ManyToOne
    @JoinColumn(name="oid")
    private Order order;

    @ManyToOne
    @JoinColumn(name="uid")

    private User user;
    //商品订单购买数量
    private int number;


}
