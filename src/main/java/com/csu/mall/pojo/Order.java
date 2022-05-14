package com.csu.mall.pojo;

import com.csu.mall.service.OrderService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @ManyToOne
    @JoinColumn(name="uid")

    private User user;


    private String orderCode;
    private String address;
    private String post;
    private String receiver;
    private String mobile;
    private String userMessage;
    private Date createDate;
    private Date payDate;
    private Date deliveryDate;
    private Date confirmDate;
    private String status;

    //一条订单项可以有多个商品
    @Transient
    private List<OrderItem> orderItems;
    @Transient
    private float total;
    @Transient
    private int totalNumber;
    @Transient
    private String statusDesc;

    //除此之外，还提供了一个getStatusDesc方法，用于把英文表达的Status信息转换为中文：
    public String getStatusDesc(){
        if(null!=statusDesc)
            return statusDesc;
        String desc ="未知";
        switch(status){
            case OrderService.waitPay:
                desc="待付";
                break;
            case OrderService.waitDelivery:
                desc="待发";
                break;
            case OrderService.waitConfirm:
                desc="待收";
                break;
            case OrderService.waitReview:
                desc="等评";
                break;
            case OrderService.finish:
                desc="完成";
                break;
            case OrderService.delete:
                desc="刪除";
                break;
            default:
                desc="未知";
        }
        statusDesc = desc;
        return statusDesc;
    }
}
