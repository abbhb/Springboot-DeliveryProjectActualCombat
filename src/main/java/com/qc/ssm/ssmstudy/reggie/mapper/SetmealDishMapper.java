package com.qc.ssm.ssmstudy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

//    /**
//     * 批量插入套餐绑定菜品
//     * INSERT INTO setmeal_dish (id, setmeal_id, dish_id, name, price, copies, sort, create_time, update_time, create_user, update_user, is_deleted, store_id) VALUES
//     * @param setmealDishList
//     * @return
//     */
//    int insertBatchSetmealDish(@Param("sd") List<SetmealDish> setmealDishList);
}
