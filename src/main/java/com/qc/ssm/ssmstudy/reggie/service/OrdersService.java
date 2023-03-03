package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.OrdersDtoResult;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Orders;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;

import java.time.LocalDateTime;

public interface OrdersService extends IService<Orders> {
    OrdersDto submit(OrdersDto ordersDto);

    /**
     * 订单更新通用接口
     * @param tradeNo
     * @param ordersStatusToBeDelivered
     * @param localDateTime
     * @param ordersPayWayZFB
     * @return
     */
    boolean updateState(String tradeNo, Integer ordersStatusToBeDelivered, LocalDateTime localDateTime, Integer ordersPayWayZFB);

    R<PageData<OrdersDtoResult>> listForAdmin(Integer pageNum, Integer pageSize, Long storeId, String consignee, Integer orderStatus, Integer payStatus);
}
