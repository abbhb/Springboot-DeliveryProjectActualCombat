package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.StoreResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @NeedToken
    @PostMapping("/add")
    public R<StoreResult> addStore(@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> store){
//        String userId = (String) store.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        String storeName = (String) store.get("storeName");
        String storeIntroduction = (String) store.get("storeIntroduction");
        String storeStatus = (String) store.get("storeStatus");
        return storeService.addStore(caozuoId,storeName,storeIntroduction,storeStatus,token);
    }
    @NeedToken
    @GetMapping("/get")
    public R<PageData> getStoreList(Integer pageNum, Integer pageSize, String name){
        log.info("pageNum = {},pageSize = {},name = {}",pageNum,pageSize,name);

        return storeService.getStoreList(pageNum,pageSize,name);
    }

    @NeedToken
    @GetMapping("/getstorelistonlyidwithname")
    public R<List<ValueLabelResult>> getStoreListOnlyIdWithName(){
        return storeService.getStoreListOnlyIdWithName();
    }
    @NeedToken
    @PostMapping("/updatastorestatus")
    public R<StoreResult> updataStoreStatus(@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> store){
//        String userId = (String) store.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        String storeId = (String) store.get("storeId");
        String storeStatus = (String) store.get("storeStatus");
        return storeService.updataStoreStatus(caozuoId,storeId,storeStatus,token);
    }
    @NeedToken
    @PostMapping("/updata")
    public R<StoreResult> updataStore(@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> store){
//        String userId = (String) store.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        String storeId = (String) store.get("storeId");
        String storeName = (String) store.get("storeName");
        String storeIntroduction = (String) store.get("storeIntroduction");
        String storeStatus = (String) store.get("storeStatus");
        return storeService.updataStore(caozuoId,storeId,storeName,storeIntroduction,storeStatus,token);
    }
    @NeedToken
    @PostMapping("/deletestore")
    public R<StoreResult> deleteStore(@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> store){
//        String userId = (String) store.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        String storeId = (String) store.get("storeId");
        return storeService.deleteStore(caozuoId,storeId,token);
    }

//    //后期加权限注解
//    @NeedToken
//    @PostMapping("/getliststore")
//    public R<List<StoreResult>> getListStoreForSelect(@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> store){
//        return storeService.getListStoreForSelect();
//    }

    //后期加权限注解
    @NeedToken
    @PostMapping("/getstorebyid")
    public R<StoreResult> getStoreById(@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> store){
        String storeid = (String) store.get("storeid");
        return storeService.getStoreById(storeid);
    }

}
