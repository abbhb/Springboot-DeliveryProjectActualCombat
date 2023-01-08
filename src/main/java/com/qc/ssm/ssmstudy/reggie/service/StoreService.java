package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.StoreResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Store;

import java.util.List;

public interface StoreService extends IService<Store> {

    R<StoreResult> addStore(String userId, String storeName, String storeIntroduction, String storeStatus,String token);

    R<PageData> getStoreList(Integer pageNum, Integer pageSize, String name);

    R<StoreResult> updataStoreStatus(String userId, String storeId, String storeStatus,String token);

    R<StoreResult> updataStore(String userId, String storeId, String storeName, String storeIntroduction, String storeStatus,String token);

    R<StoreResult> deleteStore(String userId, String storeId, String token);

    R<List<ValueLabelResult>> getStoreListOnlyIdWithName();


    R<StoreResult> getStoreById(String storeid);
}
