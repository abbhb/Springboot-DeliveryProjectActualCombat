package com.qc.ssm.ssmstudy.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("store_id")
    private Long storeId;//默认雪花算法生成id

    private String storeName;

    private String storeIntroduction;

    private Integer storeStatus;

    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDelete;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
