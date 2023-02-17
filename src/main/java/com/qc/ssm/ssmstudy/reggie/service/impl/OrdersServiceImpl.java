package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.mapper.OrdersMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Orders;
import com.qc.ssm.ssmstudy.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

}
