package com.qc.ssm.ssmstudy.reggie.pojo.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细
 */
@Data
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId("id")//设置默认主键
    private Long id;

    //名称
    private String name;

    //图片
    private String image;

    //订单id
    private Long orderId;

    //type
    private Integer type;

    //菜品id
    private Long dishId;


    //套餐id
    private Long setmealId;


    //口味
    private String dishFlavor;


    //数量
    private Integer number;

    //金额
    private BigDecimal amount;

    //归属门店
    private Long storeId;

    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;


}
