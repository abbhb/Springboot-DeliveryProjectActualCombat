package com.qc.ssm.ssmstudy.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    int upDataTest();
}
