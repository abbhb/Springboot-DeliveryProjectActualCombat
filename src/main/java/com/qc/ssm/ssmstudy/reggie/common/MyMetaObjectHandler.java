package com.qc.ssm.ssmstudy.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

//元数据处理器
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = Long.valueOf(httpServletRequest.getHeader("userid"));
        log.info("公共字段自动填充");
        //此处可能会出现异常,要是userid为null此处异常
        metaObject.setValue("createUser",userId);
        metaObject.setValue("updateUser",userId);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("isDelete",Integer.valueOf(0));//刚插入默认都是不删除的
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充");
        Long userId = Long.valueOf(httpServletRequest.getHeader("userid"));
        //此处可能会出现异常,要是userid为null此处异常
        metaObject.setValue("updateUser",userId);
        metaObject.setValue("updateTime",LocalDateTime.now());
    }
}
