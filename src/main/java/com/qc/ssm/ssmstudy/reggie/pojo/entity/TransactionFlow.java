package com.qc.ssm.ssmstudy.reggie.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流水
 */
@Data
public class TransactionFlow implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    //订单名
    private String subject;

    //订单状态
    private String tradeStatus;

    //交易流水号，支付平台生成
    private Long tradeNo;


    //商户订单号(对应订单id)
    private Long outTradeNo;

    //交易金额
    private String amount;

    //购买者的id
    private Long buyerId;

    //买家付款时间
    private LocalDateTime gmtPayment;

    //买家付款金额
    private String buyerPayAmount;
}
