package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.CategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Category;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;

import java.util.List;

public interface CategoryService extends IService<Category> {
    R<String> saveCategory(Category category);

    R<PageData<CategoryResult>> getCategoryPage(String pageNum, String pageSize,String storeId);

    R<String> deleteCategory(String id);

    R<String> updataCategory(Category category);

    R<List<ValueLabelResult>> getCategoryLableValueList(String storeId,String type);

    R<List<CategoryResult>> getCategoryList(Long storeId);
}
