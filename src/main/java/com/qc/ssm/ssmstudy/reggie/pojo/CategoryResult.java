package com.qc.ssm.ssmstudy.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Slf4j
@AllArgsConstructor
public class CategoryResult  implements Serializable {
    private static final long serialVersionUID = 1L;

    //分类的Id
    private String id;//返回给前端不能直接使用Long


    //类型 1 菜品分类 2 套餐分类
    private Integer type;


    //分类名称
    private String name;


    //顺序
    private Integer sort;

    //门店Id
    private String storeId;//返回给前端不能直接使用Long

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String createUser;//返回给前端不能直接使用Long

    private String updateUser;//返回给前端不能直接使用Long

    private Integer version;

}
