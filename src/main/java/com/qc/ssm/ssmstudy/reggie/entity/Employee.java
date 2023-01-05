package com.qc.ssm.ssmstudy.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
//@AllArgsConstructor//不加这个是没有有参构造的
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    //value属性用于指定主键的字段
    //type属性用于设置主键生成策略，默认雪花算法
    @TableId("id")//设置默认主键
    private Long id;

    private String username;

    private String name;

    private String password;

    private String salt;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;
    @TableField(fill = FieldFill.INSERT)//只在插入时填充
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)//这些注解都是调用basemapper才有用,自己写的sql不会生效，插入和更新时都填充
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)//只在插入时填充
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//这些注解都是调用basemapper才有用,自己写的sql不会生效，插入和更新时都填充
    private Long updateUser;


    @TableField(fill = FieldFill.INSERT)
    @TableLogic//如果加了这个字段就说明这个表里默认都是假删除，mp自带的删除方法都是改状态为1，默认0是不删除。自定义的mybatis不知道是不是这样
    private Integer isDelete;

//    @Version//乐观锁
//    private Integer version;//测试完毕，注解只能通过mybatisplus才生效，通过原来的mybatis不生效，得手写,用户信息没必要加锁

    private Integer permissions;//权限，1为admin，2为门店管理员，3为门店员工，2,3必须有对应的门店id

    private Long storeId;//绑定门店Id，非必要
}
