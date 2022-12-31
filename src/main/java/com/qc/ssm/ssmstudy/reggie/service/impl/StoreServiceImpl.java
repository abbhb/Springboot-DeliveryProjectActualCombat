package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.entity.Store;
import com.qc.ssm.ssmstudy.reggie.mapper.StoreMapper;
import com.qc.ssm.ssmstudy.reggie.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {
    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public R<StoreResult> addStore(Long idl, String name, String introduction, Integer status) {
        if (name==null){
            return R.error("店名不能为空");
        }
        if (status==null){
            return R.error("状态不能为空");
        }
        if (name.equals("")){
            return R.error("店名不能为空");
        }
        Store store = new Store();
        store.setIsDelete(0);
        store.setStoreName(name);
        store.setStoreIntroduction(introduction);
        store.setStoreStatus(status);
        storeMapper.insert(store);
        return null;
    }
}
