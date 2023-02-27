package com.qc.ssm.ssmstudy.reggie.common;

public class Code {
    public final static Integer SUCCESS = 1;

    public final static Integer DEL_TOKEN = 900;

    public final static Integer ERROR = 0;

    public final static Integer ToIndex = 2;

    /**
     * 订单状态码
     * 1-->待付款
     */
    public final static Integer OrdersStatusToBePaid = 1;

    /**
     * 订单状态码
     * 2-->待派送
     */
    public final static Integer  OrdersStatusToBeDelivered = 2;

    /**
     * 订单状态码
     * 3-->已派送
     */
    public final static Integer  OrdersStatusDelivered = 3;

    /**
     * 订单状态码
     * 4-->已完成
     */
    public final static Integer  OrdersStatusCompleted = 4;

    /**
     * 订单状态码
     * 5-->已取消
     */
    public final static Integer  OrdersStatusCanceled = 5;

    /**
     * 微信方式支付
     */
    public final static Integer OrdersPayWayWX = 1;

    /**
     * 支付宝方式支付
     */
    public final static Integer OrdersPayWayZFB = 2;

}

