package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealDish;

import java.util.Collection;
import java.util.List;

public interface SetmealDishService extends IService<SetmealDish> {
    Collection<Long> getSetmealDishOnlyIdListBySetmealIdList(Collection<SetmealDish> listBig);

    R<List<DishResult>> getSetmealDish(Long id);
}
