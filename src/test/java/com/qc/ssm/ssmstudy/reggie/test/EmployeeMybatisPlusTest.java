package com.qc.ssm.ssmstudy.reggie.test;

import com.qc.ssm.ssmstudy.reggie.entity.Employee;
import com.qc.ssm.ssmstudy.reggie.mapper.EmployeeMapper;
import com.qc.ssm.ssmstudy.reggie.utils.PWDMD5;
import com.qc.ssm.ssmstudy.reggie.utils.RedisOperator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EmployeeMybatisPlusTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RedisOperator redisOperator;

    @Test
    public void testFind(){
        List<Employee> employees = employeeMapper.selectList(null);

        //若没有条件设置，将条件设置为null
        employees.forEach(System.out::println);
    }
    @Test
    public void testPWDMD5(){
        String salt = PWDMD5.getSalt();
        System.out.println("salt = " + salt);
        String qc123456 = PWDMD5.getMD5Encryption("qc123456", salt);
        System.out.println("qc123456 = " + qc123456);

    }
    @Test
    public void testUpdata(){
//        int i = employeeMapper.upDataTest();
//        System.out.println(i);
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("admin");
        employee.setSex("男");
        employee.setUpdateUser(1L);
        employee.setVersion(1);
        employeeMapper.updateById(employee);
    }
    @Test
    public void testRedis(){
//        int i = employeeMapper.upDataTest();
//        System.out.println(i);
        String notcunzai = redisOperator.get("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOjF9.Yqe1AZGZNLwXVlSTFOuvB27kF51bezdcjck106zbf9M");
        if (notcunzai!=null){

        }else {
            //不存在key
        }
    }
}
