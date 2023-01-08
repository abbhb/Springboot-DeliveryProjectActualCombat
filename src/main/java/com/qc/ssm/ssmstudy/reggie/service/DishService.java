package com.qc.ssm.ssmstudy.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;

public interface DishService extends IService<Dish> {
    R<String> addDish(DishResult dishResult);
}
