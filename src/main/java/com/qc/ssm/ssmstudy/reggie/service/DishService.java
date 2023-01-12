package com.qc.ssm.ssmstudy.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishAndCategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;

import java.util.List;

public interface DishService extends IService<Dish> {
    R<String> addDish(DishResult dishResult);

    R<PageData<DishAndCategoryResult>> getDish(Integer pageNum,Integer pageSize,Long storeId,String name);

    R<String> updateStatus(String id, String status);

    R<String> deleteDishAndFlavor(String id);

    R<String> editDish(DishResult dishResult);

    R<List<DishResult>> getDishListByCategoryId(Long categoryId, Long storeId,String name);
}
