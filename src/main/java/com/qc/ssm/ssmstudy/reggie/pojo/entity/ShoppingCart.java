package com.qc.ssm.ssmstudy.reggie.pojo.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //名称
//    private String name;

    //用户id
    private Long userId;

    //菜品还是套餐,dish:1,setmeal:2
    private Integer type;

    //菜品id
    private Long dishId;

    //套餐id
    private Long setmealId;

    //口味,拼接字符串就行
    private String dishFlavor;

    //数量
    private Integer number;

    //金额
//    private BigDecimal amount;

    //图片
//    private String image;

    private Integer version;//用来标记版本是否更新了，因为数据都是实时更新的

    private LocalDateTime createTime;

    private Long storeId;//商店Id
}
