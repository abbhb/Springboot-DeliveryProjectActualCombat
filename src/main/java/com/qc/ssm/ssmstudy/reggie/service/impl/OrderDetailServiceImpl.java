package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.common.annotation.StoreStateDetection;
import com.qc.ssm.ssmstudy.reggie.common.exception.CustomException;
import com.qc.ssm.ssmstudy.reggie.mapper.OrderDetailMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.dto.OrdersDto;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.OrderDetail;
import com.qc.ssm.ssmstudy.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

    /**
     * 停售的门店拒绝提交
     * 其余的数据以此处提交为准
     * @param ordersDto
     * @return
     */
    //加入AOP，判断商店是否是关闭或者删除
    @Override
    @StoreStateDetection
    @Transactional
    public R<String> submit(OrdersDto ordersDto) {
        if (ordersDto.getOrderDetails()==null){
            throw new CustomException("Details-->err-->o");
        }
        if (ordersDto.getId()==null){
            throw new CustomException("Details-->err-->i");
        }
        log.info("orderDetail = {}",ordersDto.getOrderDetails());
        for (OrderDetail orderDetail:
                ordersDto.getOrderDetails()) {
            orderDetail.setOrderId(ordersDto.getId());
            orderDetail.setStoreId(ordersDto.getStoreId());
            if (orderDetail.getOrderId()==null|| (!StringUtils.isNotEmpty(orderDetail.getName())) ||orderDetail.getStoreId()==null||orderDetail.getAmount()==null||orderDetail.getType()==null||orderDetail.getNumber()==null){
                throw new CustomException("参数缺少");
            }
            orderDetail.setId(null);
            boolean save = super.save(orderDetail);
            if (!save){
                throw new CustomException("保存失败");
            }
        }
        return R.success("添加成功");
    }
}
