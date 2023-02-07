package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealFlavor;

import java.util.Collection;
import java.util.List;

public interface SetmealFlavorService extends IService<SetmealFlavor> {
    /**
     * 仅获得口味id列表，通过setmealIdList
     * @param setmealFlavors
     * @return
     */
    Collection<Long> getSetmealFlavorOnlyIdListBySetmealIdList(Collection<Setmeal> setmeal);

    /**
     * 获得口味通过dishID
     * @param id
     * @return
     */
    R<List<SetmealFlavorResult>> getSetmealFlavorResultBySetmealId(String id);
}
