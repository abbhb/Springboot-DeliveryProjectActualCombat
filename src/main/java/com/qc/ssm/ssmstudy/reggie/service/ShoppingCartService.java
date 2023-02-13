package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.ShoppingCartResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<List<ShoppingCartResult>> getList(Long userId, Long storeId);
}
