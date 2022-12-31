package com.qc.ssm.ssmstudy.reggie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeResult {
    private String id;

    private String username;

    private String name;

    private String phone;

    private String sex;//性别

    private String idNumber;//身份证号

    private Integer permissions;//权限，1为admin，2为员工

    private String token;//token

}
