package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    R<EmployeeResult> login(String username, String password);

    R<EmployeeResult> logout(String token);

    R<EmployeeResult> loginByToken(String token);


    R<EmployeeResult> updataForUser(Long id, String username, String name, String sex, String idNumber, String phone,String token);

    R<EmployeeResult> changePassword(String id, String username, String password, String newpassword, String checknewpassword, String token);
}
