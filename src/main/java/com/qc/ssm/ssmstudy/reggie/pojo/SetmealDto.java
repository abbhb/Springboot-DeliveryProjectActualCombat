package com.qc.ssm.ssmstudy.reggie.pojo;

import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class SetmealDto extends Setmeal implements Serializable {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
