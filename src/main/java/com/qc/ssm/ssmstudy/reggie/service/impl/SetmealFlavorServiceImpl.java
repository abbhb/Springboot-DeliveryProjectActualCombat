package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.SetmealFlavorMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealFlavor;
import com.qc.ssm.ssmstudy.reggie.service.SetmealFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class SetmealFlavorServiceImpl extends ServiceImpl<SetmealFlavorMapper, SetmealFlavor> implements SetmealFlavorService {

    @Override
    public Collection<Long> getSetmealFlavorOnlyIdListBySetmealIdList(Collection<Setmeal> setmeal) {
        Collection<Setmeal> setmealList = setmeal;
        Collection<Long> setmealFlavorsOnlyId = new ArrayList<>();
        for (Setmeal s:
                setmealList) {
            LambdaQueryWrapper<SetmealFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(SetmealFlavor::getId).eq(SetmealFlavor::getSetmealId,s.getId());
            List<SetmealFlavor> list = super.list(queryWrapper);
            for (SetmealFlavor d:
                    list) {
                setmealFlavorsOnlyId.add(d.getId());
            }
        }
        return setmealFlavorsOnlyId;
    }

    @Override
    public R<List<SetmealFlavorResult>> getSetmealFlavorResultBySetmealId(String id) {
        LambdaQueryWrapper<SetmealFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SetmealFlavor::getSetmealId,SetmealFlavor::getId,SetmealFlavor::getName,SetmealFlavor::getValue);
        queryWrapper.eq(SetmealFlavor::getSetmealId,id);
        List<SetmealFlavor> list = super.list(queryWrapper);
        List<SetmealFlavorResult> setmealFlavorResultList = new ArrayList<>();
        for (SetmealFlavor setmealFlavor:
                list) {
            SetmealFlavorResult setmealFlavorResult = new SetmealFlavorResult();
            setmealFlavorResult.setSetmealId(String.valueOf(setmealFlavor.getSetmealId()));
            setmealFlavorResult.setName(setmealFlavor.getName());
            setmealFlavorResult.setId(String.valueOf(setmealFlavor.getId()));
            setmealFlavorResult.setValue(setmealFlavor.getValue());
            setmealFlavorResultList.add(setmealFlavorResult);
        }
        if (setmealFlavorResultList.size()==0){
            return R.error("查询失败");
        }
        return R.success(setmealFlavorResultList);
    }
}
