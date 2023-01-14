package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
@RequestMapping(value = "/employee")
@Api(value = "employee-controller")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    @ApiOperation(value = "员工登录接口")
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
    public R<String> updataEmployeeStatus(@RequestHeader(value="Authorization", defaultValue = "") String token, @RequestBody Map<String, Object> employee){
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
    @PostMapping("/updataemployee")
    public R<String> updataEmployee(@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestBody Map<String, Object> employee){
//        String userId = (String) employee.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
       // caozuoId//操作者id
//        return employeeService.deleteEmployee(userId,caozuoId,token);
       // return employeeService.updataEmployee(caozuoId,)
        log.info(employee.toString());
        String name = (String) employee.get("name");

        String username = (String) employee.get("username");
        String phone = (String) employee.get("phone");
        String idNumber = (String) employee.get("idNumber");
        String status = (String) employee.get("status");
        String permissions = (String) employee.get("permissions");
        String storeId = (String) employee.get("storeId");
        String sex = (String) employee.get("sex");
        String userid = (String) employee.get("userid");
        return employeeService.updataEmployee(userid,name,username,phone,idNumber,status,permissions,storeId,sex,token);
    }

    @NeedToken
    @PostMapping("/add")
    public R<EmployeeResult> addEmployee(@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestBody Map<String, Object> employee){
        log.info(employee.toString());
        String name = (String) employee.get("name");
        String username = (String) employee.get("username");
        String password = (String) employee.get("password");//前端传回的未加密的密码
        String phone = (String) employee.get("phone");
        String idNumber = (String) employee.get("idNumber");
        String status = (String) employee.get("status");
        String permissions = (String) employee.get("permissions");
        String storeId = (String) employee.get("storeId");
        String sex = (String) employee.get("sex");
        //String userid = (String) employee.get("userid");//员工id由雪花算法生成
        return employeeService.addEmployee(caozuoId,name,username,password,phone,idNumber,status,permissions,storeId,sex,token);
    }

    @NeedToken
    @GetMapping("/get")
    public R<PageData> getEmployeeList(Integer pageNum, Integer pageSize, String name,@RequestHeader(value="userid", defaultValue = "") String caozuoId){
        log.info("pageNum = {},pageSize = {},name = {},caozuoId = {}",pageNum,pageSize,name,caozuoId);
        return employeeService.getEmployeeList(pageNum,pageSize,name,caozuoId);
    }

    @NeedToken
    @GetMapping("/getemployeelistonlyidwithname")
    public R<List<ValueLabelResult>> getEmployeeListOnlyIdWithName(@RequestHeader(value="userid", defaultValue = "") String caozuoId){
        return employeeService.getEmployeeListOnlyIdWithName(caozuoId);
    }

    @NeedToken
    @PostMapping("/deleteemployee")
    public R<EmployeeResult> deleteEmployee(@RequestHeader(value="Authorization", defaultValue = "") String token,@RequestHeader(value="userid", defaultValue = "") String caozuoId,@RequestBody Map<String, Object> employee){
        String userId = (String) employee.get("userId");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        return employeeService.deleteEmployee(userId,caozuoId,token);
    }
}
