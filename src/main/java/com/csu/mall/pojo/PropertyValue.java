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
@Table(name="propertyvalue")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class PropertyValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    //属性值的pid对应产品
    @ManyToOne
    @JoinColumn(name="pid")
    private Product product;

    //属性值的ptid对应属性
    @ManyToOne
    @JoinColumn(name="ptid")
    private Property property;

    private String value;

    @Override
    public String toString() {
        return "PropertyValue [id=" + id + ", product=" + product + ", property=" + property + ", value=" + value + "]";
    }


}
