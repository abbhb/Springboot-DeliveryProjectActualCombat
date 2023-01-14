package com.qc.ssm.ssmstudy.reggie.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SetmealAndCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //分类id
    private Long categoryId;


    //套餐名称
    private String name;


    //套餐价格
    private BigDecimal price;


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

    private Long createUser;

    private Long updateUser;

    private Integer isDeleted;

    private Integer version;


    private Long storeId;

    private Integer sort;//排序

    //类型 1 菜品分类 2 套餐分类
    private Integer categoryType;

    private Integer categorySort;
    //分类名称
    private String categoryName;

    private Integer categoryVersion;

}
