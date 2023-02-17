package com.qc.ssm.ssmstudy.reggie.pojo.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.OrderDetail;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 包含Orders的全部属性,为了使用controller的自动映射,不方便用继承
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersDto implements Serializable {

    private static final long serialVersionUID = 1L;
    //订单号 id
    private Long id;

    //订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;


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

    //用户名
    private String userName;

    //手机号
    private String phone;

    //地址
    private String address;

    //商店id
    private Long storeId;

    //收货人(冗余地址里的收货人)
    private String consignee;


    /**
     * 状态备注
     * 比如订单取消的原因
     */
    private String statusRemark;

    private List<OrderDetail> orderDetails;
}
