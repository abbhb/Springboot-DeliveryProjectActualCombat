package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.DishMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.DishAndCategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.pojo.vo.DishAndCategoryVO;
import com.qc.ssm.ssmstudy.reggie.service.DishFlavorService;
import com.qc.ssm.ssmstudy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishMapper dishMapper;

    @Transactional
    @Override
    public R<String> addDish(DishResult dishResult) {
        if (dishResult.getStoreId()==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        String imageUrl = "";
        if (dishResult.getName()==null){
            return R.error("菜品名不能为空");
        }
        if (dishResult.getStatus()==null){
            return R.error("状态必须选择");
        }
        if (dishResult.getCategoryId()==null){
            return R.error("分类必须选择");
        }
        if (dishResult.getSort()==null){
            return R.error("排序不能为空");
        }
        if (dishResult.getPrice()==null){
            return R.error("价钱不能为空");
        }
        if (dishResult.getImage()==null){
            imageUrl = "";
        }
        imageUrl = dishResult.getImage();
        if (dishResult.getDishFlavors()==null){
            return R.error("至少需要包含一种口味");
        }

        Dish dish = new Dish();
        dish.setName(dishResult.getName());
        BigDecimal bigDecimal = new BigDecimal(dishResult.getPrice());
        bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);//小数位数2位，四舍五入法
        dish.setPrice(bigDecimal);
        dish.setSort(dishResult.getSort());
        dish.setStatus(dishResult.getStatus());
        dish.setStoreId(Long.valueOf(dishResult.getStoreId()));
        dish.setCategoryId(Long.valueOf(dishResult.getCategoryId()));
        dish.setImage(imageUrl);
        dish.setSaleNum(0L);//刚创建销量为0
        dish.setDescription(dishResult.getDescription()==null?"": dishResult.getDescription());

        //添加菜品
        boolean save = dishService.save(dish);//save完成后会自动装入id
        if (!save){
            throw new CustomException("业务异常");
        }

        //添加口味
        for (Object obj:
                dishResult.getDishFlavors()) {
            Map<String,Object> dishFlavorResult = (Map<String, Object>) obj;
            System.out.println(dishFlavorResult.toString());
            DishFlavor dishFlavor = new DishFlavor();
            dishFlavor.setDishId(dish.getId());//save完成后会自动装入id
            dishFlavor.setStoreId(Long.valueOf(dishResult.getStoreId()));
            dishFlavor.setName((String) dishFlavorResult.get("name"));
            dishFlavor.setValue(String.valueOf((List) dishFlavorResult.get("value")));
            boolean save1 = dishFlavorService.save(dishFlavor);
            if (!save1){
                throw new CustomException("业务异常");
            }
        }
        return R.success("保存成功");
    }

    @Override
    public R<PageData<DishAndCategoryResult>> getDish(Integer pageNum,Integer pageSize,Long storeId,String name) {
        Integer pageNumD = 1;
        Integer pageSizeD = 10;
        if (pageNum!=null){
            pageNumD = pageNum;
        }
        if (pageSize!=null){
            pageSizeD = pageSize;
        }
        Page<DishAndCategoryVO> pageInfo = new Page<>(pageNumD,pageSizeD);
        QueryWrapper<DishAndCategoryVO> dishAndCategoryVOWrapper = new QueryWrapper<>();
        dishAndCategoryVOWrapper.eq("dish.store_id",storeId);
        if (name!=null){
            dishAndCategoryVOWrapper.like("dish.name",name);//给查询动态加条件
        }
//            <!--这里注意，自己写的sql，得注意mp的逻辑删除都不能用,包括增改的自动填充，所以得手写条件-->
        dishAndCategoryVOWrapper.eq("dish.is_deleted",0);//需要未删除的数据
        dishMapper.getDishAndCategoryVO(pageInfo,dishAndCategoryVOWrapper);
        if (pageInfo==null){
            return R.error("查询错误");
        }
        List<DishAndCategoryVO> dishAndCategoryVOList = pageInfo.getRecords();
        PageData<DishAndCategoryResult> dishAndCategoryResultPageData = new PageData<>();
        dishAndCategoryResultPageData.setCountId(pageInfo.getCountId());
        dishAndCategoryResultPageData.setPages(pageInfo.getPages());
        dishAndCategoryResultPageData.setSize(pageInfo.getSize());
        dishAndCategoryResultPageData.setTotal(pageInfo.getTotal());
        dishAndCategoryResultPageData.setCurrent(pageInfo.getCurrent());
        dishAndCategoryResultPageData.setMaxLimit(pageInfo.getMaxLimit());
        List<DishAndCategoryResult> dishAndCategoryResults = new ArrayList<>();
        for (DishAndCategoryVO dish :
                dishAndCategoryVOList) {

            DishAndCategoryResult dishAndCategoryResult = new DishAndCategoryResult(String.valueOf(dish.getId()),dish.getName(),String.valueOf(dish.getCategoryId()),String.valueOf(dish.getPrice()),dish.getCode(),dish.getImage(),dish.getDescription(),String.valueOf(dish.getStatus()),String.valueOf(dish.getSort()),dish.getCreateTime(),dish.getUpdateTime(),String.valueOf(dish.getCreateUser()),String.valueOf(dish.getUpdateUser()),String.valueOf(dish.getStoreId()),String.valueOf(dish.getVersion()),String.valueOf(dish.getCategoryType()),String.valueOf(dish.getCategorySort()),dish.getCategoryName(),String.valueOf(dish.getVersion()));

            dishAndCategoryResults.add(dishAndCategoryResult);
        }
        dishAndCategoryResultPageData.setRecords(dishAndCategoryResults);
//        log.info(pageInfo.getRecords().toString());
        return R.success(dishAndCategoryResultPageData);
    }

    @Override
    @Transactional
    public R<String> updateStatus(String id, String status) {
        log.info("id = {},status = {}",id,status);
        Collection<Dish> entityList = new ArrayList<>();
        String[] split = id.split(",");

        for(int i =0; i < split.length ; i++){
            Dish dish = new Dish();
            dish.setId(Long.valueOf(split[i]));
            dish.setStatus(Integer.valueOf(status));
            entityList.add(dish);
        }
        boolean b = dishService.updateBatchById(entityList);
        if (b){
            return R.success("更新成功");
        }
        return R.error("更新失败");
    }

    @Override
    @Transactional
    public R<String> deleteDishAndFlavor(String id) {
        log.info("id = {}",id);
        Collection<Long> longList = new ArrayList<>();
        Collection<DishFlavor> listBig = new ArrayList<>();
        String[] split = id.split(",");
        for(int i =0; i < split.length ; i++){
            longList.add(Long.valueOf(split[i]));
            DishFlavor dishFlavor = new DishFlavor();
            dishFlavor.setDishId(Long.valueOf(split[i]));
            listBig.add(dishFlavor);
        }
        /**
         * notebook:这里批量删除Id是什么类型就用泛型就填什么，直接放入实体类会报错
         */
        boolean b = dishService.removeByIds(longList);
        Collection<Long> dishFlavorOnlyIdListByDishIdList = dishFlavorService.getDishFlavorOnlyIdListByDishIdList(listBig);
        if (dishFlavorOnlyIdListByDishIdList.size()==0){
            return R.success("删除成功");
        }
        boolean b1 = dishFlavorService.removeByIds(dishFlavorOnlyIdListByDishIdList);
        if (!b){
            throw new CustomException("业务异常:deleteDishAndFlavor");
        }
        if (!b1){
            throw new CustomException("业务异常:deleteDishAndFlavor1");
        }

        return R.success("删除成功");
    }

    @Override
    @Transactional
    public R<String> editDish(DishResult dishResult) {
        if (dishResult.getId()==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        if (dishResult.getStoreId()==null){
            //反正也不允许改门店，爱空空吧
        }
        String imageUrl = "";
        if (dishResult.getName()==null){
            return R.error("菜品名不能为空");
        }
        if (dishResult.getStatus()==null){
            return R.error("状态必须选择");
        }
        if (dishResult.getCategoryId()==null){
            return R.error("分类必须选择");
        }
        if (dishResult.getSort()==null){
            return R.error("排序不能为空");
        }
        if (dishResult.getPrice()==null){
            return R.error("价钱不能为空");
        }
        if (dishResult.getVersion()==null){
            return R.error("版本号不能为空");
        }
        if (dishResult.getImage()==null){
            imageUrl = "";
        }
        imageUrl = dishResult.getImage();
        if (dishResult.getDishFlavors()==null){
            return R.error("至少需要包含一种口味");
        }
        if (dishResult.getDishFlavors().size()==0){
            return R.error("至少需要包含一种口味");
        }
        //口味不再设置版本号，乐观锁作用在食品即可，食品口味版本不对直接回滚
//        if (dishResult.getFlavorVersion()==null){
//            return R.error("口味版本异常,请重试!");
//
//        }


        Dish dish = new Dish();

        dish.setName(dishResult.getName());
        BigDecimal bigDecimal = new BigDecimal(dishResult.getPrice());
        bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);//小数位数2位，四舍五入法
        dish.setPrice(bigDecimal);
        dish.setSort(dishResult.getSort());
        dish.setStatus(dishResult.getStatus());
        dish.setCategoryId(Long.valueOf(dishResult.getCategoryId()));
        dish.setImage(imageUrl);
        dish.setDescription(dishResult.getDescription()==null?"": dishResult.getDescription());
        dish.setVersion(Integer.valueOf(dishResult.getVersion()));//更新成功让口味版本号+1(只用传入原版本号，mp会自动帮我们递增)
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId,Long.valueOf(dishResult.getId()));
        //version
        log.info("version = {}",dishResult.getVersion());
//            updateWrapper.eq(Dish::getVersion,dishResult.getVersion());//校验版本号
        //更新菜品
        boolean update = dishService.update(dish, updateWrapper);
        if (!update){
            throw new CustomException("业务异常:dishService:update");
        }
        //删除原来的口味
        /**
         * 直接改方案，全删口味在新增，不然容易出问题
         */
        Long dishId = Long.valueOf(dishResult.getId());//当前菜品的Id
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(DishFlavor::getId).eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        Collection<Long> longCollection = new ArrayList<>();
        for (DishFlavor d:
             list) {
            longCollection.add(d.getId());
        }
        if (longCollection.size()!=0){//如果本来就没有口味就不需要删除了
            boolean b = dishFlavorService.removeByIds(longCollection);
            if (!b){
                throw new CustomException("业务异常:removeByIds");
            }
        }
        //添加口味
        for (Object obj:
                dishResult.getDishFlavors()) {
            Map<String,Object> dishFlavorResult = (Map<String, Object>) obj;
            System.out.println(dishFlavorResult.toString());
            DishFlavor dishFlavor = new DishFlavor();
            dishFlavor.setDishId(Long.valueOf(dishResult.getId()));//和新增不同，此处是对已有菜品进行更改，所以dishId得直接从上面取
            dishFlavor.setStoreId(Long.valueOf(dishResult.getStoreId()));//冗余数据
            dishFlavor.setName((String) dishFlavorResult.get("name"));
            dishFlavor.setValue(String.valueOf((List) dishFlavorResult.get("value")));
            boolean save1 = dishFlavorService.save(dishFlavor);
            if (!save1){
                throw new CustomException("业务异常:dishFlavorService:save");
            }
        }
        return R.success("更新成功");
    }

    @Override
    public R<List<DishResult>> getDishListByCategoryId(Long categoryId, Long storeId,String name) {
        if (storeId == null){
            return R.error("业务异常");
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if (categoryId!=null){
            queryWrapper.eq(Dish::getCategoryId,categoryId);
        }
        queryWrapper.eq(Dish::getStoreId,storeId);
        if (name!=null){
            if ((!name.equals(""))&&(!name.equals("undefined"))&&(!name.equals("null"))){
                queryWrapper.like(Dish::getName,name);
            }
        }
        List<Dish> list = dishService.list(queryWrapper);
        if (list==null){
            return R.error("业务异常");
        }
        List<DishResult> dishResults = new ArrayList<>();
        for (Dish dish:
             list) {
            DishResult dishResult = new DishResult();
            dishResult.setId(String.valueOf(dish.getId()));
            dishResult.setName(dish.getName());
            dishResult.setImage(dish.getImage());
            dishResult.setPrice(String.valueOf(dish.getPrice()));
            dishResult.setStatus(dish.getStatus());
            dishResult.setVersion(String.valueOf(dish.getVersion()));
            //其实我感觉应该把每个菜的version传入，新增的时候判断下此时的菜品表是没有被更改的,
            dishResults.add(dishResult);
        }
        return R.success(dishResults);
    }

    /**
     * @param categoryId
     * @param storeId
     * @return
     */
    @Override
    public R<List<DishResult>> getDishList(Long categoryId, Long storeId) {
        if (storeId == null){
            return R.error("业务异常");
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if (categoryId!=null){
            queryWrapper.eq(Dish::getCategoryId,categoryId);
        }
        queryWrapper.eq(Dish::getStoreId,storeId);
        queryWrapper.orderByAsc(Dish::getSort);
        List<Dish> list = dishService.list(queryWrapper);
        if (list==null){
            return R.error("业务异常");
        }
        List<DishResult> dishResults = new ArrayList<>();
        for (Dish dish:
                list) {
            //返回每一个是否有口味
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dish.getId());
            List<DishFlavor> list1 = dishFlavorService.list(queryWrapper1);
            List<DishFlavorResult> list2 = new ArrayList<>();
            for (DishFlavor dishFlavor:
                    list1) {
                DishFlavorResult dishFlavorResult = new DishFlavorResult();
                BeanUtils.copyProperties(dishFlavor,dishFlavorResult);
                list2.add(dishFlavorResult);
            }
            DishResult dishResult = new DishResult();
            dishResult.setId(String.valueOf(dish.getId()));
            dishResult.setName(dish.getName());
            dishResult.setImage(dish.getImage());
            dishResult.setPrice(String.valueOf(dish.getPrice()));
            dishResult.setStatus(dish.getStatus());
            dishResult.setFlavors(list2);
            dishResult.setVersion(String.valueOf(dish.getVersion()));
            dishResult.setSaleNum(String.valueOf(dish.getSaleNum()));
            //其实我感觉应该把每个菜的version传入，新增的时候判断下此时的菜品表是没有被更改的,
            dishResults.add(dishResult);
        }
        return R.success(dishResults);
    }
}
