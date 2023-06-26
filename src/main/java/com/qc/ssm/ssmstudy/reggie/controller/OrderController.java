package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.annotation.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.OrdersDtoResult;
import com.qc.ssm.ssmstudy.reggie.pojo.OrdersResult;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
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

    /**
     * 查询订单(管理端专用)
     * Get-->Page-->分页-->storeId(必传),还可以加一些筛选条件
     * return OrdersDtoResult
     * consignee:收货人
     * orderStatus:订单状态
     * payStatus:支付状态
     */
    @GetMapping("/listForAdmin")
    @NeedToken
    public R<PageData<OrdersDtoResult>> list(@RequestParam(value = "pageSize",required = true) Integer pageSize, @RequestParam(value = "pageNum",required = true) Integer pageNum, @RequestParam(value = "storeId",required = true) Long storeId, @RequestParam(value = "consignee",required = false) String consignee, @RequestParam(value = "orderStatus",required = false) Integer orderStatus, @RequestParam(value = "payStatus",required = false) Integer payStatus){

        return ordersService.listForAdmin(pageNum,pageSize,storeId,consignee,orderStatus,payStatus);
    }
    @GetMapping("/userPage")
    @NeedToken
    public R<PageData<OrdersDtoResult>> userPage(Integer pageSize,Integer pageNum){
        return R.success(null);
    }

}
