package com.qc.ssm.ssmstudy.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;

import java.util.List;

public interface EmployeeService extends IService<Employee> {
    R<EmployeeResult> login(String username, String password);

    R<EmployeeResult> logout(String token);

    R<EmployeeResult> loginByToken(String token);


    R<EmployeeResult> updataForUser(Long id, String username, String name, String sex, String idNumber, String phone,String token);

    R<EmployeeResult> changePassword(String id, String username, String password, String newpassword, String checknewpassword, String token);

    R<PageData> getEmployeeList(Integer pageNum, Integer pageSize, String name,String caozuoId);

    R<String> updataEmployeeStatus(String userId, String caozuoId, String userStatus, String token);

    R<EmployeeResult> deleteEmployee(String userId, String caozuoId, String token);

    R<String> updataEmployee(String caozuoId, String userid, String name, String username, String phone, String idNumber, String status, String permissions, String storeId, String sex, String token);

    R<EmployeeResult> addEmployee(String caozuoId, String name, String username, String password, String phone, String idNumber, String status, String permissions, String storeId, String sex, String token);

    R<List<ValueLabelResult>> getEmployeeListOnlyIdWithName(String caozuoId);
}
