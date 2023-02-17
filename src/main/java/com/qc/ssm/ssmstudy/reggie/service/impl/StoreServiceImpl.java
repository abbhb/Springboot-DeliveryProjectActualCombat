package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.CustomException;
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
//        store.setStoreCreateTime(LocalDateTime.now());
//        store.setStoreUpdataTime(LocalDateTime.now());
//        store.setStoreCreateUserId(Long.valueOf(userId));
//        store.setStoreUpdataUserId(Long.valueOf(userId));
//        store.setIsDelete(0);
        boolean save = super.save(store);
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
        lambdaQueryWrapper.orderByDesc(Store::getCreateTime);//按照创建时间排序
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
//        store.setStoreUpdataUserId(Long.valueOf(userId));
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
//        store.setStoreUpdataTime(LocalDateTime.now());
        boolean update = super.update(store, updateWrapper);
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
//        store.setStoreUpdataUserId(Long.valueOf(userId));
        store.setStoreName(storeName);
        store.setStoreStatus(Integer.valueOf(storeStatus));
        store.setStoreIntroduction(storeIntroduction);
        boolean update = super.update(store, updateWrapper);
        if (update){
            return R.success("更新成功");
        }
        return R.error("未知异常");
    }

    @Override
    @Transactional
    public R<StoreResult> deleteStore(String userId, String storeId, String token) {
        if (!StringUtils.isNotEmpty(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("参数异常");
        }
        DecodedJWT decodedJWT = JWTUtil.deToken(token);
        Claim id = decodedJWT.getClaim("id");

        String tokenId = id.asString();
        if (!StringUtils.isNotEmpty(tokenId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (Long.valueOf(tokenId)!=Long.valueOf(userId)){
            iStringRedisService.del(token);
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        boolean b = super.removeById(Long.valueOf(storeId));
        //感觉异步会更好
        //删除跟此商店关联的数据
        //分类数据
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getStoreId,Long.valueOf(storeId));
        categoryService.remove(categoryLambdaQueryWrapper);
        //与此商店绑定的菜品口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getStoreId,Long.valueOf(storeId));
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //与此商店绑定的菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getStoreId,Long.valueOf(storeId));
        dishService.remove(dishLambdaQueryWrapper);
        //与此商店绑定的员工解绑，不直接删除
        LambdaUpdateWrapper<Employee> employeeLambdaQueryWrapper = new LambdaUpdateWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getStoreId,Long.valueOf(storeId));
        employeeLambdaQueryWrapper.set(Employee::getStoreId,null);//将所有员工的门店绑定先给去掉
        employeeService.update(employeeLambdaQueryWrapper);
        //套餐菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getStoreId,Long.valueOf(storeId));
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //套餐口味
        LambdaQueryWrapper<SetmealFlavor> setmealFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealFlavorLambdaQueryWrapper.eq(SetmealFlavor::getStoreId,Long.valueOf(storeId));
        setmealFlavorService.remove(setmealFlavorLambdaQueryWrapper);
        //套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getStoreId,Long.valueOf(storeId));
        setmealService.remove(setmealLambdaQueryWrapper);
        //购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getStoreId,Long.valueOf(storeId));
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        if (b){
            return R.success("删除成功");
        }

        return R.error("系统异常,请刷新重试!");
    }

    @Override
    public R<List<ValueLabelResult>> getStoreListOnlyIdWithName(String caozuoId) {
        if (!StringUtils.isNotEmpty(caozuoId)){
            return R.error("环境遗产");
        }
        Employee byId = employeeService.getById(Long.valueOf(caozuoId));
        if (byId==null){
            throw new CustomException("环境异常");
        }

        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Store::getStoreId,Store::getStoreName);
        if (byId.getPermissions()==2){
            queryWrapper.eq(Store::getStoreId,byId.getStoreId());//如果权限为门店管理，就只返回指定门店
        }else if (byId.getPermissions()==3){
            throw new CustomException("异常");
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
        return R.error("获取失败");
    }



    @Override
    public R<StoreResult> getStoreById(String storeid) {
        LambdaQueryWrapper<Store> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Store::getStoreId,Store::getStoreName,Store::getStoreIntroduction,Store::getStoreStatus);
        queryWrapper.eq(Store::getStoreId,Long.valueOf(storeid));
        Store store = super.getOne(queryWrapper);
        if (store==null){
            return R.error("查询失败");
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
            return R.error("异常");
        }
        Store byId = super.getById(storeId);
        if (byId==null){
            return R.error("异常");
        }
        StoreResult storeResult = new StoreResult();
        storeResult.setId(String.valueOf(byId.getStoreId()));
        storeResult.setStatus(byId.getStoreStatus());
        storeResult.setIntroduction(byId.getStoreIntroduction());
        storeResult.setName(byId.getStoreName());
        return R.success(storeResult);
    }
}
