package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    R<PageData<SetmealResult>> getSetmeal(Integer pageNum, Integer pageSize, Long storeId, String name);

    R<String> addSetmeal(SetmealResult setmealResult);

    R<String> updateStatus(String id, String status);

    R<String> deleteSetmeal(String id);

    R<String> editSetmeal(SetmealResult setmealResult);

    R<List<SetmealResult>> getSetmealList(Long categoryId, Long storeId);
}
