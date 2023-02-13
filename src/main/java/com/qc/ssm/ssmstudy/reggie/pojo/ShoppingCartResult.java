package com.qc.ssm.ssmstudy.reggie.pojo;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ShoppingCartResult implements Serializable {

    private String id;

    //名称
    private String name;

    //用户id
    private String userId;

    //type
    private Integer type;

    //菜品id
    private String dishId;

    //套餐id
    private String setmealId;

    //口味
    private String dishFlavor;

    //数量
    private Integer number;
    

    private Integer version;//用来标记版本是否更新了，因为数据都是实时更新的

    private LocalDateTime createTime;

    private String amount;//价钱

    //图片
    private String image;

}
