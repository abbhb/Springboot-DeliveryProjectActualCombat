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

    @TableLogic
    private Integer isDelete;

    private LocalDateTime storeCreateTime;

    private LocalDateTime storeUpdataTime;

    @TableField(fill = FieldFill.INSERT)
    private Long storeCreateUserId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long storeUpdataUserId;

}
