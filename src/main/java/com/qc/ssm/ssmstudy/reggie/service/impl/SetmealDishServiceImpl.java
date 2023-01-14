package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.SetmealDishMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealDish;
import com.qc.ssm.ssmstudy.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public Collection<Long> getSetmealDishOnlyIdListBySetmealIdList(Collection<SetmealDish> listBig) {
        Collection<Long> setmealDishOnlyId = new ArrayList<>();
        for (SetmealDish setmealDish:
                listBig) {
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(SetmealDish::getId).eq(SetmealDish::getSetmealId,setmealDish.getSetmealId());
            List<SetmealDish> list = setmealDishService.list(queryWrapper);
            for (SetmealDish d:
                    list) {
                setmealDishOnlyId.add(d.getId());
            }
        }
        return setmealDishOnlyId;
    }

    @Override
    public R<List<DishResult>> getSetmealDish(Long id) {
        if (id==null){
            throw new CustomException("业务异常");
        }
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        List<DishResult> dishResults = new ArrayList<>();
        if (list==null){
            throw new CustomException("业务异常");
        }
        if (list.size()==0){
            return R.success("空~");
        }
        for (SetmealDish s:
             list) {
            DishResult dishResult = new DishResult();
            dishResult.setId(String.valueOf(s.getId()));
            dishResult.setCopies(s.getCopies());
            dishResult.setName(s.getName());
            dishResult.setPrice(String.valueOf(s.getPrice()));
//            dishResult.setImage(s.getImage());
            dishResults.add(dishResult);
        }
        return R.success(dishResults);
    }
}
