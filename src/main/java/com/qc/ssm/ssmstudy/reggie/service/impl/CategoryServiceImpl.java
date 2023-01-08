package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.CategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Category;
import com.qc.ssm.ssmstudy.reggie.mapper.CategoryMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.service.CategoryService;
import com.qc.ssm.ssmstudy.reggie.service.DishService;
import com.qc.ssm.ssmstudy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public R<String> saveCategory(Category category) {
        //防止注入其他参数
        Category category1 = new Category();
        category1.setSort(category.getSort());
        category1.setName(category.getName());
        category1.setStoreId(category.getStoreId());
        category1.setType(category.getType());
        boolean save = categoryService.save(category1);
        if (save){
            return R.success("新建成功");
        }
        return R.error("服务异常");
    }

    @Override
    public R<PageData<CategoryResult>> getCategoryPage(String pageNum, String pageSize,String storeId) {
        if (!StringUtils.isNotEmpty(pageNum)){
            return R.error("分页参数缺失");
        }
        if (!StringUtils.isNotEmpty(pageSize)){
            return R.error("分页参数缺失");
        }
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("没有找到任何门店");
        }
        Page<Category> pageinfo = new Page<>(Integer.valueOf(pageNum),Integer.valueOf(pageSize));
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStoreId,Long.valueOf(storeId));
        queryWrapper.orderByAsc(Category::getSort,Category::getName);
        queryWrapper.select(Category::getId,Category::getName,Category::getType,Category::getSort,Category::getCreateTime,Category::getCreateUser,Category::getUpdateTime,Category::getUpdateUser,Category::getVersion,Category::getStoreId);
        categoryService.page(pageinfo, queryWrapper);
        List<Category> categoryList = pageinfo.getRecords();
        List<CategoryResult> categoryResults = new ArrayList<>();
        for (Category category: categoryList) {
            CategoryResult categoryResult = new CategoryResult(String.valueOf(category.getId()), category.getType(),category.getName(), category.getSort(),String.valueOf(category.getStoreId()),category.getCreateTime(),category.getUpdateTime(),String.valueOf(category.getCreateUser()),String.valueOf(category.getUpdateUser()),category.getVersion());
            categoryResults.add(categoryResult);
        }
        PageData<CategoryResult> categoryResultPageData = new PageData<>();
        categoryResultPageData.setPages(pageinfo.getPages());
        categoryResultPageData.setTotal(pageinfo.getTotal());
        categoryResultPageData.setCurrent(pageinfo.getCurrent());
        categoryResultPageData.setRecords(categoryResults);
        categoryResultPageData.setSize(pageinfo.getSize());
        categoryResultPageData.setCountId(pageinfo.getCountId());

        if (categoryResults!=null){
            return R.success(categoryResultPageData);
        }
        return R.error("服务异常");
    }

    @Override
    public R<String> deleteCategory(String id) {
        //判断有没有绑定的套餐和菜品
        if (!StringUtils.isNotEmpty(id)){
            //返回业务异常，这里其实可以直接返回R.err,但是练习下自定义异常
            throw new CustomException("参数异常");//感觉多此一举
        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,Long.valueOf(id));
        int count = dishService.count(queryWrapper);
        if (count>0){
            throw new CustomException("当前分类绑定了菜品，请先删除相关菜品");
//            return R.error("当前分类绑定了菜品，请先删除相关菜品");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,Long.valueOf(id));
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1>0){
            return R.error("当前分类绑定了套餐，请先删除相关套餐");
        }
        boolean b = categoryService.removeById(Long.valueOf(id));
        if (b){
            return R.success("删除成功");
        }
        throw new CustomException("业务异常");

    }

    @Override
    public R<String> updataCategory(Category category) {
        if (category.getId()==null){
            return R.error("参数不全");
        }
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId,category.getId());
        updateWrapper.set(Category::getSort,category.getSort());
        updateWrapper.set(Category::getName,category.getName());
        updateWrapper.set(Category::getType,category.getType());
        //此处手动更新，防止利用接口传入更多的参数
        boolean update = categoryService.update(updateWrapper);
        if (update){
            return R.success("更新成功");
        }
        return R.error("服务异常");
    }

    @Override
    public R<List<ValueLabelResult>> getCategoryLableValueList(String storeId,String type) {
        if (!StringUtils.isNotEmpty(storeId)){
            return R.error("参数异常");
        }
        if (!StringUtils.isNotEmpty(type)){
            return R.error("参数异常");
        }
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Category::getId,Category::getName);
        queryWrapper.eq(Category::getStoreId,Long.valueOf(storeId)).eq(Category::getType,Integer.valueOf(type));
        List<Category> list = categoryService.list(queryWrapper);
        if (list==null){
            return R.error("服务异常");
        }
        List<ValueLabelResult> valueLabelResultList = new ArrayList<>();
        for (Category category :
                list) {
            ValueLabelResult valueLabelResult = new ValueLabelResult(String.valueOf(category.getId()),category.getName());
            valueLabelResultList.add(valueLabelResult);
        }

        return R.success(valueLabelResultList);
    }
}
