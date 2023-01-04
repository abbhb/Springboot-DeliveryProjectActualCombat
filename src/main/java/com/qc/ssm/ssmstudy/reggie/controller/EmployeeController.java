package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.entity.PageData;
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

    @NeedToken
    @PostMapping("/updataemployeestatus")
    public R<EmployeeResult> updataEmployeeStatus(@RequestHeader(value="Authorization", defaultValue = "") String token, @RequestBody Map<String, Object> employee){
        String userId = (String) employee.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        String caozuoId = (String) employee.get("caozuoId");//操作人
        String userStatus = (String) employee.get("userStatus");
        return employeeService.updataEmployeeStatus(userId,caozuoId,userStatus,token);
    }

    @NeedToken
    @PostMapping("/updataforuser")
    public R<EmployeeResult> updataForUser(@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> user){
        System.out.println("user = " + user);
        String id = (String) user.get("id");
        String username = (String) user.get("username");
        String name = (String) user.get("name");
        String sex = (String) user.get("sex");
        String idNumber = (String) user.get("idNumber");
        String phone = (String) user.get("phone");
        if (id==null){
            return R.error("更新失败");
        }
        if (username==null){
            return R.error("更新失败");
        }
        if (name==null){
            return R.error("更新失败");
        }
        if (sex==null){
            return R.error("更新失败");
        }if (idNumber==null){
            return R.error("更新失败");
        }
        if (phone==null){
            return R.error("更新失败");
        }
        try {
            return employeeService.updataForUser(Long.valueOf(id),username,name,sex,idNumber,phone,token);
        }catch (Exception e){
            return R.error("更新失败");
        }
    }


    @PostMapping("/changepassword")
    public R<EmployeeResult> changePassword(@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> user){
        System.out.println("user = " + user);
        String id = (String) user.get("id");
        String username = (String) user.get("username");
        String password = (String) user.get("password");
        String newpassword = (String) user.get("newpassword");
        String checknewpassword = (String) user.get("checknewpassword");
        return employeeService.changePassword(id,username,password,newpassword,checknewpassword,token);
    }

    @NeedToken
    @GetMapping("/get")
    public R<PageData> getEmployeeList(Integer pageNum, Integer pageSize, String name){
        log.info("pageNum = {},pageSize = {},name = {}",pageNum,pageSize,name);
        return employeeService.getEmployeeList(pageNum,pageSize,name);
    }

    @NeedToken
    @PostMapping("/deleteemployee")
    public R<EmployeeResult> deleteEmployee(@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> employee){
        String userId = (String) employee.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        String caozuoId = (String) employee.get("caozuoId");//操作人
        return employeeService.deleteEmployee(userId,caozuoId,token);
    }
}
