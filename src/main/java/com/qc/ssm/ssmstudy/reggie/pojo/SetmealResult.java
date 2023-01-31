package com.qc.ssm.ssmstudy.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SetmealResult {
    private static final Long serialVersionUID = 1L;

    private String id;


    //分类id
    private String categoryId;


    //套餐名称
    private String name;


    //套餐价格
    private String price;


    //状态 0:停用 1:启用
    private Integer status;


    //编码
    private String code;


    //描述信息
    private String description;


    //图片
    private String image;

    
    private LocalDateTime createTime;
    

    private LocalDateTime updateTime;

    
    private String createUser;

    
    private String updateUser;


    //是否删除
    private Integer isDeleted;
    
    private Integer version;
    
    private String storeId;

    private Integer sort;//排序

    private String categoryName;

    /**
     * 销量
     */
    private String saleNum;

    private List<DishResult> dishResults;//套餐中的菜品
}
