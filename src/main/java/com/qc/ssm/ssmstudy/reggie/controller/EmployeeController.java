package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * RestController:包含了@ResponseBody @Controller 两个注解
 * @Controller 一般应用在有返回界面的应用场景下.
 *
 * 例如，管理后台使用了 thymeleaf 作为模板开发，需要从后台直接返回 Model 对象到前台，那么这时候就需要使用 @Controller 来注解。
 *
 * @RestController 如果只是接口，那么就用 RestController 来注解.
 *
 * 例如前端页面全部使用了 Html、Jquery来开发，通过 Ajax 请求服务端接口，那么接口就使用 @RestController 统一注解。
 */


/**
 * /login-->login-->Post
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<EmployeeResult> login(@RequestBody Map<String, Object> user){
        System.out.println("user = " + user);
        String username = (String) user.get("username");
        String password = (String) user.get("password");
        return employeeService.login(username,password);
    }
    @NeedToken
    @PostMapping("/logout")
    public R<EmployeeResult> logout(@RequestHeader(value="Authorization", defaultValue = "") String token){
        return employeeService.logout(token);
    }

    @PostMapping("/loginbytoken")
    public R<EmployeeResult> loginByToken(@RequestHeader(value="Authorization", defaultValue = "") String token){

        return employeeService.loginByToken(token);
    }

}
