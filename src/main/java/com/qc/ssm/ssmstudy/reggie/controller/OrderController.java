package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.ShoppingCart;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping(value = "/order")
@Api(value = "order-controller")
public class OrderController {

    /**
     * 创建订单
     * 以用户提交订单页的数据为准，在此之后更新的数据不管
     */
    @PostMapping("/submit")
    @NeedToken
    public R<String> submit(@RequestHeader(value="userid", required = true)String userid, @RequestBody OrdersDto ordersDto) {

//        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("ordersDto:{}", ordersDto);
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
        return null;
    }
}
