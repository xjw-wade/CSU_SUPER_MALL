package com.csu.mall.dto;

import lombok.Data;

@Data
public class UpdateAddressDTO {
    private Integer id;

    private String recipient; //收件人
    private String phone; //手机号
    private String province; //省
    private String city; //城市
    private String district; //区
    private String post; //邮编
    private String detail; //具体地址
}
