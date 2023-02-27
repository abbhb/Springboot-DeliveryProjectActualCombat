package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.common.annotation.StoreStateDetection;
import com.qc.ssm.ssmstudy.reggie.common.exception.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.exception.ToIndexException;
import com.qc.ssm.ssmstudy.reggie.mapper.OrdersMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.AddressBook;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.OrderDetail;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Orders;
import com.qc.ssm.ssmstudy.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final ShoppingCartService shoppingCartService;

    private final AddressBookService addressBookService;


    private final OrderDetailService orderDetailService;

    @Autowired
    public OrdersServiceImpl(ShoppingCartService shoppingCartService, AddressBookService addressBookService, OrderDetailService orderDetailService) {
        this.shoppingCartService = shoppingCartService;
        this.addressBookService = addressBookService;
        this.orderDetailService = orderDetailService;
    }

    /**
     * 停售的门店拒绝提交
     * 其余的数据以此处提交为准
     * @param ordersDto
     * @return
     */
    @Override
    //加入AOP，判断商店是否是关闭或者删除
    @StoreStateDetection
    @Transactional
    public OrdersDto submit(OrdersDto ordersDto) {
        if (ordersDto.getAddressBookId()==null){
            throw new CustomException("地址err");
        }
        if (ordersDto.getAmount()==null){
            throw new CustomException("amounterr");
        }
        if (ordersDto.getStoreId()==null){
            throw new CustomException("storeIderr");
        }
        if (ordersDto.getUserId()==null){
            throw new CustomException("userIderr");
        }
        //现在前面备份数据
        Collection<Long> ids = new ArrayList<>();
        for (OrderDetail orderDetail:
                ordersDto.getOrderDetails()) {
            ids.add(orderDetail.getId());
        }
        //根据addressId获取username
        AddressBook addressBook = addressBookService.getById(ordersDto.getAddressBookId());
        //此处注意，传入订单的都是地址表里的信息，不是用户表
        Orders orders = new Orders();
        orders.setAddress(addressBook.getAbout() + addressBook.getDetail()+ "[" + addressBook.getLabel() + "]");
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Code.OrdersStatusToBePaid);//刚创建的时候就是待付款状态,对应1
        orders.setOrderStatus(Code.OrdersStatusToBePaid);
        orders.setAmount(ordersDto.getAmount());
        orders.setConsignee(ordersDto.getConsignee());
        orders.setAddressBookId(ordersDto.getAddressBookId());
        orders.setPhone(addressBook.getPhone());
        orders.setRemark(ordersDto.getRemark());
        orders.setStatusRemark("用户创建订单");
        orders.setStoreId(ordersDto.getStoreId());
        orders.setUserId(ordersDto.getUserId());
        orders.setConsignee(addressBook.getConsignee());//获取地址里的name
        //添加订单信息
        boolean save = super.save(orders);
        ordersDto.setId(orders.getId());
        //添加订单详情
        orderDetailService.submit(ordersDto);//如果有一个地方出了问题都回滚，然后返回异常
        //此处submit会改数据，所以得在此之前备份
        if (save){
            //清空购物车,最好异步
            shoppingCartService.cleanByIds(ids);
            return ordersDto;
        }
        throw new ToIndexException("提交订单失败");
    }

    /**
     * @param tradeNo                   -->id
     * @param ordersStatusToBeDelivered -->状态
     * @param localDateTime
     * @param ordersPayWayZFB
     * @return
     */
    @Transactional
    @Override
    public boolean updateState(String tradeNo, Integer ordersStatusToBeDelivered, LocalDateTime localDateTime, Integer ordersPayWayZFB) {
        if (!StringUtils.isNotEmpty(tradeNo)){
            return false;
        }
        log.info("开始更新");
        LambdaUpdateWrapper<Orders> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Orders::getCheckoutTime,localDateTime);
        lambdaUpdateWrapper.set(Orders::getPayMethod,ordersPayWayZFB);
        if (ordersStatusToBeDelivered>=2){
            lambdaUpdateWrapper.set(Orders::getPayStatus,2);
        }
        lambdaUpdateWrapper.set(Orders::getPayStatus,ordersStatusToBeDelivered);
        lambdaUpdateWrapper.set(Orders::getOrderStatus,ordersStatusToBeDelivered);
        lambdaUpdateWrapper.eq(Orders::getId,Long.valueOf(tradeNo));

        boolean update = super.update(lambdaUpdateWrapper);
        log.info("更新结果{}",update);
        if (update){
            return true;
        }
        return false;
    }
}
