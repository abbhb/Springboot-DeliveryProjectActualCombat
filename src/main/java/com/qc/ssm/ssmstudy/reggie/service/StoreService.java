package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.entity.Store;

public interface StoreService extends IService<Store> {
    R<StoreResult> addStore(Long idl, String name, String introduction, Integer status);
}
