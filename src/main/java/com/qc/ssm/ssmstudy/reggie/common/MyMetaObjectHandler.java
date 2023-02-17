package com.qc.ssm.ssmstudy.reggie.common;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.qc.ssm.ssmstudy.reggie.utils.JWTUtil;
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
//        Long userId = Long.valueOf(httpServletRequest.getHeader("userid"));
        String authorization =  httpServletRequest.getHeader("Authorization");
        DecodedJWT decodedJWT = JWTUtil.deToken(authorization);
        Claim id = decodedJWT.getClaim("id");
        log.info("公共字段自动填充,id = {}",id.asString());
        //此处可能会出现异常,要是userid为null此处异常
        if (metaObject.hasSetter("createUser")){
            metaObject.setValue("createUser",Long.valueOf(id.asString()));
        }
        if (metaObject.hasSetter("updateUser")){
            metaObject.setValue("updateUser",Long.valueOf(id.asString()));
        }
        if (metaObject.hasSetter("createTime")){
            metaObject.setValue("createTime", LocalDateTime.now());
        }
        if (metaObject.hasSetter("updateTime")){
            metaObject.setValue("updateTime",LocalDateTime.now());
        }
        if (metaObject.hasSetter("isDeleted")){
            metaObject.setValue("isDeleted",Integer.valueOf(0));//刚插入默认都是不删除的
        }
        if (metaObject.hasSetter("version")){
            metaObject.setValue("version",Integer.valueOf(0));
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充");
//        Long userId = Long.valueOf(httpServletRequest.getHeader("userid"));
        try {
            String authorization =  httpServletRequest.getHeader("Authorization");
            DecodedJWT decodedJWT = JWTUtil.deToken(authorization);
            Claim id = decodedJWT.getClaim("id");
            //此处可能会出现异常,要是userid为null此处异常
            metaObject.setValue("updateUser",Long.valueOf(id.asString()));
        }catch (Exception e){
            e.printStackTrace();
        }

        metaObject.setValue("updateTime",LocalDateTime.now());
    }
}
