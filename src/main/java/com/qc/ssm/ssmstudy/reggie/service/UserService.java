package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.UserResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.User;
import org.springframework.stereotype.Service;

public interface UserService extends IService<User> {
    R<String> sendMsg(String phone);

    R<UserResult> login(String phone, String code);
}
