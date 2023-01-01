package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.entity.Store;

public interface StoreService extends IService<Store> {

    R<StoreResult> addStore(String userId, String storeName, String storeIntroduction, String storeStatus,String token);

    R<PageData> getStoreList(Integer pageNum, Integer pageSize, String name);

    R<StoreResult> updataStoreStatus(String userId, String storeId, String storeStatus,String token);

    R<StoreResult> updataStore(String userId, String storeId, String storeName, String storeIntroduction, String storeStatus,String token);

    R<StoreResult> deleteStore(String userId, String storeId, String token);
}
