package com.qc.ssm.ssmstudy.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DishResult implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private Integer status;

    //顺序
    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


    private String createUser;

    private String updateUser;


    private String storeId;
    
    private String version;

    private List dishFlavors;//不能指定DishFlavorResult,在口味中

    private Integer copies;//份数

    private String saleNum;//销量
}
