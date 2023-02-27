package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.annotation.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.OrdersResult;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.service.OrdersService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/order")
@Api(value = "order-controller")
public class OrderController {

    private final OrdersService ordersService;

    @Autowired
    public OrderController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /**
     * 创建订单
     * 以用户提交订单页的数据为准，在此之后更新的数据不管
     * 门店关门的话不允许创建
     * 此接口需要加密
     */
    @PostMapping("/submit")
    @NeedToken
    public R<OrdersResult> submit(@RequestHeader(value="userid", required = true)String userid, @RequestBody OrdersDto ordersDto) {
        log.info("ordersDto:{}", ordersDto);
        ordersDto.setUserId(Long.valueOf(userid));
        OrdersDto ordersDto1 = ordersService.submit(ordersDto);
        OrdersResult ordersResult = new OrdersResult();
        ordersResult.setId(String.valueOf(ordersDto1.getId()));
        ordersResult.setAmount(ordersDto1.getAmount());
        return R.success(ordersResult);
    }
}
