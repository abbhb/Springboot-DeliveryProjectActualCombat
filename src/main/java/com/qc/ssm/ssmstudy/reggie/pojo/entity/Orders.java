package com.qc.ssm.ssmstudy.reggie.pojo.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    //订单号 id
    @TableId("id")//设置默认主键
    private Long id;

    //付款状态 1待付款，2已付款
    private Integer payStatus;


    //订单状态 1待支付 2待派送，3已派送，4已完成，5已取消
    private Integer orderStatus;

    //下单用户id
    private Long userId;

    //地址id
    private Long addressBookId;


    //下单时间
    private LocalDateTime orderTime;


    //结账时间
    private LocalDateTime checkoutTime;


    //支付方式 1微信，2支付宝
    private Integer payMethod;


    //实收金额
    private BigDecimal amount;

    //备注
    private String remark;

    //手机号
    private String phone;

    //地址
    private String address;

    //商店id
    private Long storeId;

    //收货人(冗余地址里的收货人)
    private String consignee;

    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

    /**
     * 状态备注
     * 比如订单取消的原因
     */
    private String statusRemark;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;
}
