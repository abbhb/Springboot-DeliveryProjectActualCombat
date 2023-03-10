package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.exception.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.StoreResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.*;
import com.qc.ssm.ssmstudy.reggie.mapper.StoreMapper;
import com.qc.ssm.ssmstudy.reggie.service.*;
import com.qc.ssm.ssmstudy.reggie.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    private final EmployeeService employeeService;

    private final IStringRedisService iStringRedisService;

    private final CategoryService categoryService;

    private final DishFlavorService dishFlavorService;

    private final DishService dishService;

    private final SetmealDishService setmealDishService;

    private final SetmealFlavorService setmealFlavorService;

    private final SetmealService setmealService;

    private final ShoppingCartService shoppingCartService;

    @Autowired
    public StoreServiceImpl(EmployeeService employeeService, IStringRedisService iStringRedisService, CategoryService categoryService, DishFlavorService dishFlavorService, DishService dishService, SetmealDishService setmealDishService, SetmealFlavorService setmealFlavorService, SetmealService setmealService, ShoppingCartService shoppingCartService) {
        this.employeeService = employeeService;
        this.iStringRedisService = iStringRedisService;
        this.categoryService = categoryService;
        this.dishFlavorService = dishFlavorService;
        this.dishService = dishService;
        this.setmealDishService = setmealDishService;
        this.setmealFlavorService = setmealFlavorService;
        this.setmealService = setmealService;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    public R<StoreResult> addStore(String userId, String storeName, String storeIntroduction, String storeStatus,String token) {
        String storeIntroductions = storeIntroduction;
        if (userId==null){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"???????????????????????????");
            // ????????????????????????token??????id??????????????????????????????????????????
            // ??????????????????token????????????token??????????????????ip????????????
        }
        if (storeName==null){
            return R.error("??????????????????");
        }
        if (storeName.equals("")){
            return R.error("??????????????????");
        }
        if (storeIntroduction==null){
            storeIntroductions = "";
        }
        if (storeIntroduction.equals("")){
            storeIntroductions = "";
        }
        if (storeStatus==null){
            return R.error("??????????????????");
        }
        if (storeStatus.equals("")){
            return R.error("??????????????????");
        }
        Store store = new Store();
        Integer status = null;

        store.setStoreStatus(Integer.valueOf(storeStatus));
        store.setStoreName(storeName);
        store.setStoreIntroduction(storeIntroductions);
//        store.setStoreCreateTime(LocalDateTime.now());
//        store.setStoreUpdataTime(LocalDateTime.now());
//        store.setStoreCreateUserId(Long.valueOf(userId));
//        store.setStoreUpdataUserId(Long.valueOf(userId));
//        store.setIsDelete(0);
        boolean save = super.save(store);
        if (save){
            return R.successOnlyMsg("????????????",Code.SUCCESS);
        }
        return R.error("????????????");
    }

    @Override
    public R<PageData> getStoreList(Integer pageNum, Integer pageSize, String name) {
        if (pageNum==null){
            return R.error("????????????");
        }
        if (pageSize==null){
            return R.error("????????????");
        }
        Page pageInfo = new Page(pageNum,pageSize);
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //?????????????????????
        lambdaQueryWrapper.select(Store::getStoreId,Store::getStoreStatus,Store::getStoreIntroduction,Store::getStoreName);
        //??????????????????
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Store::getStoreName,name);
        //??????????????????
        lambdaQueryWrapper.orderByDesc(Store::getCreateTime);//????????????????????????
        super.page(pageInfo,lambdaQueryWrapper);
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
        pageData.setMaxLimit(pageInfo.getMaxLimit());
        return R.success(pageData);
    }

    @Override
    public R<StoreResult> updataStoreStatus(String userId, String storeId, String storeStatus,String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"???????????????????????????");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("??????????????????????????????");
        }
        if (!StringUtils.isNotEmpty(storeStatus)){
            return R.error("??????????????????????????????");
        }
        LambdaUpdateWrapper<Store> updateWrapper = new LambdaUpdateWrapper<>();
        //??????????????????
        updateWrapper.eq(Store::getStoreId,Long.valueOf(storeId));
        Store store = new Store();
        store.setStoreId(Long.valueOf(storeId));
//        store.setStoreUpdataUserId(Long.valueOf(userId));
        //????????????????????????1??????0
        Integer integer = null;
        if (Integer.valueOf(storeStatus)==0){
            integer = 1;
        }else if (Integer.valueOf(storeStatus)==1){
            integer = 0;
        }else {
            return R.error("????????????");
        }
        store.setStoreStatus(integer);
//        store.setStoreUpdataTime(LocalDateTime.now());
        boolean update = super.update(store, updateWrapper);
        if (update){
            return R.success("????????????");
        }
        return R.error("????????????");
    }

    @Override
    public R<StoreResult> updataStore(String userId, String storeId, String storeName, String storeIntroduction, String storeStatus,String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"????????????,????????????");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("????????????,?????????!");
        }
        if (!StringUtils.isNotEmpty(storeName)){
            return R.error("?????????????????????");
        }
        if (!StringUtils.isNotEmpty(storeStatus)){
            return R.error("????????????");
        }
        LambdaUpdateWrapper<Store> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Store::getStoreId,Long.valueOf(storeId));
        Store store = new Store();
        store.setStoreId(Long.valueOf(storeId));
//        store.setStoreUpdataUserId(Long.valueOf(userId));
        store.setStoreName(storeName);
        store.setStoreStatus(Integer.valueOf(storeStatus));
        store.setStoreIntroduction(storeIntroduction);
        boolean update = super.update(store, updateWrapper);
        if (update){
            return R.success("????????????");
        }
        return R.error("????????????");
    }

    @Override
    @Transactional
    public R<StoreResult> deleteStore(String userId, String storeId, String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"????????????,????????????");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("????????????");
        }
        DecodedJWT decodedJWT = JWTUtil.deToken(token);
        Claim id = decodedJWT.getClaim("id");

        String tokenId = id.asString();
        if (!StringUtils.isNotEmpty(tokenId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"????????????,????????????");
        }
        if (Long.valueOf(tokenId)!=Long.valueOf(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"????????????,????????????");
        }
        boolean b = super.removeById(Long.valueOf(storeId));
        //?????????????????????
        //?????????????????????????????????
        //????????????
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getStoreId,Long.valueOf(storeId));
        categoryService.remove(categoryLambdaQueryWrapper);
        //?????????????????????????????????
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getStoreId,Long.valueOf(storeId));
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //???????????????????????????
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getStoreId,Long.valueOf(storeId));
        dishService.remove(dishLambdaQueryWrapper);
        //???????????????????????????????????????????????????
        LambdaUpdateWrapper<Employee> employeeLambdaQueryWrapper = new LambdaUpdateWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getStoreId,Long.valueOf(storeId));
        employeeLambdaQueryWrapper.set(Employee::getStoreId,null);//??????????????????????????????????????????
        employeeService.update(employeeLambdaQueryWrapper);
        //????????????
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getStoreId,Long.valueOf(storeId));
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //????????????
        LambdaQueryWrapper<SetmealFlavor> setmealFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealFlavorLambdaQueryWrapper.eq(SetmealFlavor::getStoreId,Long.valueOf(storeId));
        setmealFlavorService.remove(setmealFlavorLambdaQueryWrapper);
        //??????
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getStoreId,Long.valueOf(storeId));
        setmealService.remove(setmealLambdaQueryWrapper);
        //?????????
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getStoreId,Long.valueOf(storeId));
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        if (b){
            return R.success("????????????");
        }

        return R.error("????????????,???????????????!");
    }

    @Override
    public R<List<ValueLabelResult>> getStoreListOnlyIdWithName(String caozuoId) {
        if (!StringUtils.isNotEmpty(caozuoId)){
            return R.error("????????????");
        }
        Employee byId = employeeService.getById(Long.valueOf(caozuoId));
        if (byId==null){
            throw new CustomException("????????????");
        }

        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Store::getStoreId,Store::getStoreName);
        if (byId.getPermissions()==2){
            queryWrapper.eq(Store::getStoreId,byId.getStoreId());//??????????????????????????????????????????????????????
        }else if (byId.getPermissions()==3){
            throw new CustomException("??????");
        }
        List<Store> list = super.list(queryWrapper);
        List<ValueLabelResult> storeIdNames = new ArrayList<>();
        if (list!=null){
            for (Store store :
                    list) {
                ValueLabelResult storeIdName = new ValueLabelResult(String.valueOf(store.getStoreId()),String.format("%s(ID:%s)",store.getStoreName(),store.getStoreId()));
                storeIdNames.add(storeIdName);
            }
            return R.success(storeIdNames);
        }
        return R.error("????????????");
    }



    @Override
    public R<StoreResult> getStoreById(String storeid) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Store::getStoreId,Store::getStoreName,Store::getStoreIntroduction,Store::getStoreStatus);
        queryWrapper.eq(Store::getStoreId,Long.valueOf(storeid));
        Store store = super.getOne(queryWrapper);
        if (store==null){
            return R.error("????????????");
        }
        StoreResult storeResult = new StoreResult();
        storeResult.setId(String.valueOf(store.getStoreId()));
        storeResult.setName(store.getStoreName());
        storeResult.setStatus(store.getStoreStatus());
        storeResult.setIntroduction(store.getStoreIntroduction());
        return R.success(storeResult);
    }

    @Override
    public R<StoreResult> getStoreInfo(Long storeId) {
        if (ObjectUtils.isEmpty(storeId)){
            return R.error("??????");
        }
        Store byId = super.getById(storeId);
        if (byId==null){
            return R.error("??????");
        }
        StoreResult storeResult = new StoreResult();
        storeResult.setId(String.valueOf(byId.getStoreId()));
        storeResult.setStatus(byId.getStoreStatus());
        storeResult.setIntroduction(byId.getStoreIntroduction());
        storeResult.setName(byId.getStoreName());
        return R.success(storeResult);
    }
}
