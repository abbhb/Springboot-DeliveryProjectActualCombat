package com.qc.ssm.ssmstudy.reggie.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class DishAndCategoryResult implements Serializable {
    private static final Long serialVersionUID = 1L;

    private String id;

    //菜品名称
    private String name;

    //菜品分类id
    private String categoryId;

    //菜品价格
    private String price;

    //商品码
    private String code;

    //图片
    private String image;

    //描述信息
    private String description;

    //0 停售 1 起售
    private String status;

    //顺序
    private String sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String createUser;

    private String updateUser;

    private String storeId;

    private String version;

    //类型 1 菜品分类 2 套餐分类
    private String categoryType;

    private String categorySort;
    //分类名称
    private String categoryName;

    private String categoryVersion;
}
