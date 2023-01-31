package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealResult;
import com.qc.ssm.ssmstudy.reggie.pojo.UserResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.User;
import com.qc.ssm.ssmstudy.reggie.service.UserService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/sendmsg")
    public R<String> sendMsg(@ApiParam("手机号码")@RequestParam(value = "phone",required = true) String phone){
        return userService.sendMsg(phone);
    }
    @PostMapping("/login")
    public R<UserResult> login(@RequestBody Map<String, Object> user){
        String phone = (String) user.get("phone");
        if (!StringUtils.isNotEmpty(phone)){
            return R.error("请输入手机号");
        }
        String code = (String) user.get("code");
        if (!StringUtils.isNotEmpty(code)){
            return R.error("请输入验证码");
        }
        if (code.length()!=6){
            return R.error("请输入正确的验证码");
        }
        return userService.login(phone,code);
    }
}
