package com.qc.ssm.ssmstudy.reggie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResult {
    private String id;

    private String username;

    private String name;

    private String phone;

    private String sex;//性别

    private String idNumber;//身份证号

    private Integer permissions;//权限，1为admin，2为员工

    private Integer status;//账号状态

    private Integer storeId;//绑定店铺Id
    private String token;//token

}
