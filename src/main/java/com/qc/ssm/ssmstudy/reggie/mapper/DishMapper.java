package com.qc.ssm.ssmstudy.reggie.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.vo.DishAndCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;



@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 固定写法，Page
     * DishAndCategoryVO 返回菜单列表
     * @return
     */
    IPage<DishAndCategoryVO> getDishAndCategoryVO(Page page, @Param("ew") Wrapper<DishAndCategoryVO> queryWrapper);

}
