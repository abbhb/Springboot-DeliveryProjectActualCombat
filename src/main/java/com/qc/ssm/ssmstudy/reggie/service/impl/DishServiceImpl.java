package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.common.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.DishMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.DishFlavor;
import com.qc.ssm.ssmstudy.reggie.service.DishFlavorService;
import com.qc.ssm.ssmstudy.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;


    @Override
    public R<String> addDish(DishResult dishResult) {
        if (dishResult.getStoreId()==null){
            return R.error(Code.DEL_TOKEN,"环境异常,强制下线");
        }
        String imageUrl = "";
        if (dishResult.getName()==null){
            return R.error("菜品名不能为空");
        }
        if (dishResult.getStatus()==null){
            return R.error("状态必须选择");
        }
        if (dishResult.getCategoryId()==null){
            return R.error("分类必须选择");
        }
        if (dishResult.getSort()==null){
            return R.error("排序不能为空");
        }
        if (dishResult.getImage()==null){
            imageUrl = "";
        }
        imageUrl = dishResult.getImage();
        if (dishResult.getDishFlavors()==null){
            return R.error("至少需要包含一种口味");
        }

        Dish dish = new Dish();
        dish.setName(dishResult.getName());
        dish.setPrice(dishResult.getPrice());
        dish.setSort(dishResult.getSort());
        dish.setStatus(dishResult.getStatus());
        dish.setStoreId(Long.valueOf(dishResult.getStoreId()));
        dish.setCategoryId(Long.valueOf(dishResult.getCategoryId()));
        dish.setImage(imageUrl);
        dish.setDescription(dishResult.getDescription()==null?"": dishResult.getDescription());

        //添加菜品
        boolean save = dishService.save(dish);
        if (!save){
            throw new CustomException("业务异常");
        }

        //添加口味
        for (Object obj:
                dishResult.getDishFlavors()) {
            Map<String,Object> dishFlavorResult = (Map<String, Object>) obj;
            System.out.println(dishFlavorResult.toString());
            DishFlavor dishFlavor = new DishFlavor();
            dishFlavor.setDishId(dish.getId());
            dishFlavor.setStoreId(Long.valueOf(dishResult.getStoreId()));
            dishFlavor.setName((String) dishFlavorResult.get("name"));
            dishFlavor.setValue(String.valueOf((List) dishFlavorResult.get("value")));
            boolean save1 = dishFlavorService.save(dishFlavor);
            if (!save1){
                throw new CustomException("业务异常");
            }
        }
        return R.success("保存成功");
    }
}
