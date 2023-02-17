package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.mapper.OrderDetailMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.OrderDetail;
import com.qc.ssm.ssmstudy.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {


}
