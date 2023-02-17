package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.OrdersMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Orders;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Store;
import com.qc.ssm.ssmstudy.reggie.service.OrdersService;
import com.qc.ssm.ssmstudy.reggie.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final StoreService storeService;

    public OrdersServiceImpl(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * 停售的门店拒绝提交
     * 其余的数据以此处提交为准
     * @param ordersDto
     * @return
     */
    @Override
    public R<String> submit(OrdersDto ordersDto) {
        if (ordersDto.getAddressBookId()==null){
            return R.error("地址err");
        }
        if (ordersDto.getPayMethod()==null){
           return R.error("payerr");
        }
        if (ordersDto.getAmount()==null){
            return R.error("amounterr");
        }
        if (ordersDto.getStoreId()==null){
            return R.error("storeIderr");
        }
        if (ordersDto.getOrderDetails()==null){
            return R.error("异常,请刷新!");
        }
        //判断此刻门店是否为关闭状态
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Store::getStoreId,ordersDto.getStoreId());
        lambdaQueryWrapper.eq(Store::getStoreStatus,1);//营业中,会默认查询没被删除的数据
        Store store = storeService.getOne(lambdaQueryWrapper);
        if (store==null){
            return R.error("门店已打烊!");
        }
        return null;
    }
}
