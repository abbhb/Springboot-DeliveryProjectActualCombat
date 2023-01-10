package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.DishFlavorMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;
import com.qc.ssm.ssmstudy.reggie.service.DishFlavorService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public Collection<Long> getDishFlavorOnlyIdListByDishIdList(Collection<DishFlavor> dishFlavors) {
        Collection<DishFlavor> dishFlavorList = dishFlavors;
        Collection<Long> dishFlavorsOnlyId = new ArrayList<>();
        for (DishFlavor dishFlavor:
                dishFlavorList) {
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(DishFlavor::getId).eq(DishFlavor::getDishId,dishFlavor.getDishId());
            List<DishFlavor> list = dishFlavorService.list(queryWrapper);
            for (DishFlavor d:
                 list) {
                dishFlavorsOnlyId.add(d.getId());
            }
        }
        return dishFlavorsOnlyId;
    }

    @Override
    public R<List<DishFlavorResult>> getDishFlavorResultByDishId(String id) {
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(DishFlavor::getDishId,DishFlavor::getId,DishFlavor::getName,DishFlavor::getVersion,DishFlavor::getValue);
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        List<DishFlavorResult> dishFlavorResultList = new ArrayList<>();
        for (DishFlavor dishFlavor:
                list) {
            DishFlavorResult dishFlavorResult = new DishFlavorResult();
            dishFlavorResult.setDishId(String.valueOf(dishFlavor.getDishId()));
            dishFlavorResult.setName(dishFlavor.getName());
            dishFlavorResult.setId(String.valueOf(dishFlavor.getId()));
            dishFlavorResult.setValue(dishFlavor.getValue());
            dishFlavorResult.setVersion(dishFlavor.getVersion());
            dishFlavorResultList.add(dishFlavorResult);
        }
        if (dishFlavorResultList.size()==0){
            return R.error("查询失败");
        }
        return R.success(dishFlavorResultList);
    }
}
