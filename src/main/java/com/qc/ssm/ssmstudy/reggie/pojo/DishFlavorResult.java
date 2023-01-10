package com.qc.ssm.ssmstudy.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DishFlavorResult implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id;


    //菜品id
    private String dishId;


    //口味名称
    private String name;


    //口味数据list
    private String value;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;

    private String createUser;

    private String updateUser;
    
    private String storeId;
    
    private String version;

    private boolean showOption;
}
