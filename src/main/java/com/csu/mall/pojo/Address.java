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
@Table(name = "address")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name="uid")
    private User user;

    private String recipient; //收件人
    private String phone; //手机号
    private String province; //省
    private String city; //城市
    private String district; //区
    private String post; //邮编
    private String detail; //具体地址

}
