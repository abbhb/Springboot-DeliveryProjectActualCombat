package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
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
    public R<String> submit(@RequestHeader(value="userid", required = true)String userid, @RequestBody OrdersDto ordersDto) {
        log.info("ordersDto:{}", ordersDto);
        ordersDto.setUserId(Long.valueOf(userid));

//        if (!StringUtils.isNotEmpty(userid)){
//            return R.error("userid为空");
//        }
//        shoppingCart.setUserId(Long.valueOf(userid));
//        shoppingCart.setCreateTime(LocalDateTime.now());
////        boolean save = shoppingCartService.save(shoppingCart);
////        if (!save){
////            return R.error("保存失败");
////        }
//        return R.success("保存成功");
        return ordersService.submit(ordersDto);
    }
}
