package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.StoreIdName;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.entity.Store;
import com.qc.ssm.ssmstudy.reggie.mapper.StoreMapper;
import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import com.qc.ssm.ssmstudy.reggie.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {
    @Autowired
    private StoreService storeService;

    @Autowired
    private IStringRedisService iStringRedisService;
    @Autowired
    private StoreMapper storeMapper;


    @Override
    public R<StoreResult> addStore(String userId, String storeName, String storeIntroduction, String storeStatus,String token) {
        String storeIntroductions = storeIntroduction;
        if (userId==null){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常，强制下线");
            // 暂时不校验是否与token里的id一致了，后序会加上权限限制，
            // 非超级管理的token也没用，token校验后期加上ip属地判断
        }
        if (storeName==null){
            return R.error("店名不能为空");
        }
        if (storeName.equals("")){
            return R.error("店名不能为空");
        }
        if (storeIntroduction==null){
            storeIntroductions = "";
        }
        if (storeIntroduction.equals("")){
            storeIntroductions = "";
        }
        if (storeStatus==null){
            return R.error("状态不能为空");
        }
        if (storeStatus.equals("")){
            return R.error("状态不能为空");
        }
        Store store = new Store();
        Integer status = null;

        store.setStoreStatus(Integer.valueOf(storeStatus));
        store.setStoreName(storeName);
        store.setStoreIntroduction(storeIntroductions);
        store.setStoreCreateTime(LocalDateTime.now());
        store.setStoreUpdataTime(LocalDateTime.now());
        store.setStoreCreateUserId(Long.valueOf(userId));
        store.setStoreUpdataUserId(Long.valueOf(userId));
        store.setIsDelete(0);
        boolean save = storeService.save(store);
        if (save){
            return R.successOnlyMsg("添加成功",Code.SUCCESS);
        }
        return R.error("添加失败");
    }

    @Override
    public R<PageData> getStoreList(Integer pageNum, Integer pageSize, String name) {
        if (pageNum==null){
            return R.error("传参错误");
        }
        if (pageSize==null){
            return R.error("传参错误");
        }
        Page pageInfo = new Page(pageNum,pageSize);
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //选择返回的数据
        lambdaQueryWrapper.select(Store::getStoreId,Store::getStoreStatus,Store::getStoreIntroduction,Store::getStoreName);
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Store::getStoreName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Store::getStoreCreateTime);//按照创建时间排序
        storeService.page(pageInfo,lambdaQueryWrapper);
        PageData<StoreResult> pageData = new PageData<>();
        List<StoreResult> results = new ArrayList<>();
        for (Object store : pageInfo.getRecords()) {
            Store store1 = (Store) store;
            StoreResult storeResult = new StoreResult();
            storeResult.setId(String.valueOf(store1.getStoreId()));
            storeResult.setName(store1.getStoreName());
            storeResult.setStatus(store1.getStoreStatus());
            storeResult.setIntroduction(store1.getStoreIntroduction());
            results.add(storeResult);
        }
        pageData.setPages(pageInfo.getPages());
        pageData.setTotal(pageInfo.getTotal());
        pageData.setCountId(pageInfo.getCountId());
        pageData.setCurrent(pageInfo.getCurrent());
        pageData.setSize(pageInfo.getSize());
        pageData.setRecords(results);
        return R.success(pageData);
    }

    @Override
    public R<StoreResult> updataStoreStatus(String userId, String storeId, String storeStatus,String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常，强制下线");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("操作失败，请稍后重试");
        }
        if (!StringUtils.isNotEmpty(storeStatus)){
            return R.error("操作失败，请稍后重试");
        }
        LambdaUpdateWrapper<Store> updateWrapper = new LambdaUpdateWrapper<>();
        //添加过滤条件
        updateWrapper.eq(Store::getStoreId,Long.valueOf(storeId));
        Store store = new Store();
        store.setStoreId(Long.valueOf(storeId));
        store.setStoreUpdataUserId(Long.valueOf(userId));
        //状态就两种，不是1就是0
        Integer integer = null;
        if (Integer.valueOf(storeStatus)==0){
            integer = 1;
        }else if (Integer.valueOf(storeStatus)==1){
            integer = 0;
        }else {
            return R.error("状态错误");
        }
        store.setStoreStatus(integer);
        store.setStoreUpdataTime(LocalDateTime.now());
        boolean update = storeService.update(store, updateWrapper);
        if (update){
            return R.success("调整成功");
        }
        return R.error("调整失败");
    }

    @Override
    public R<StoreResult> updataStore(String userId, String storeId, String storeName, String storeIntroduction, String storeStatus,String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("系统异常,请重试!");
        }
        if (!StringUtils.isNotEmpty(storeName)){
            return R.error("门店名不能为空");
        }
        if (!StringUtils.isNotEmpty(storeStatus)){
            return R.error("状态异常");
        }
        LambdaUpdateWrapper<Store> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Store::getStoreId,Long.valueOf(storeId));
        Store store = new Store();
        store.setStoreId(Long.valueOf(storeId));
        store.setStoreUpdataUserId(Long.valueOf(userId));
        store.setStoreName(storeName);
        store.setStoreStatus(Integer.valueOf(storeStatus));
        store.setStoreIntroduction(storeIntroduction);
        boolean update = storeService.update(store, updateWrapper);
        if (update){
            return R.success("更新成功");
        }
        return R.error("未知异常");
    }

    @Override
    public R<StoreResult> deleteStore(String userId, String storeId, String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("参数异常");
        }
        String tokenId = iStringRedisService.getTokenId(token);
        if (!StringUtils.isNotEmpty(tokenId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (Long.valueOf(tokenId)!=Long.valueOf(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        boolean b = storeService.removeById(Long.valueOf(storeId));
        if (b){
            return R.success("删除成功");
        }

        return R.error("系统异常,请刷新重试!");
    }

    @Override
    public R<List<StoreIdName>> getStoreListOnlyIdWithName() {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Store::getStoreId,Store::getStoreName);
        List<Store> list = storeService.list(queryWrapper);
        List<StoreIdName> storeIdNames = new ArrayList<>();
        if (list!=null){
            for (Store store :
                    list) {
                StoreIdName storeIdName = new StoreIdName(String.valueOf(store.getStoreId()),store.getStoreName());
                storeIdNames.add(storeIdName);
            }
            return R.success(storeIdNames);
        }
        return R.error("获取失败");
    }
}
