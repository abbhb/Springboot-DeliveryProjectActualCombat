package com.qc.ssm.ssmstudy.reggie.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.vo.SetmealAndCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    /**
     * 固定写法，Page
     * SetmealAndCategoryVO 返回套餐列表
     * @return
     */
    IPage<SetmealAndCategoryVO> getSetmealAndCategoryVO(Page page, @Param("ew") QueryWrapper<SetmealAndCategoryVO> queryWrapper);
}
