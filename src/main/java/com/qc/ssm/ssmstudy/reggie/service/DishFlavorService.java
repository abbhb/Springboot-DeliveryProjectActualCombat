package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;

import java.util.Collection;
import java.util.List;

public interface DishFlavorService extends IService<DishFlavor> {
    /**
     * 仅获得口味id列表，通过dishIdList
     * @param dishFlavors
     * @return
     */
    Collection<Long> getDishFlavorOnlyIdListByDishIdList(Collection<DishFlavor> dishFlavors);

    /**
     * 获得口味通过dishID
     * @param id
     * @return
     */
    R<List<DishFlavorResult>> getDishFlavorResultByDishId(String id);





}
