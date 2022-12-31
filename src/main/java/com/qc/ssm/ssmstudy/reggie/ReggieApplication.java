package com.qc.ssm.ssmstudy.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
//@MapperScan("com.qc.ssm.ssmstudy.reggie.mapper"),也可以在每个mapper接口上加上@mapper注解
public class ReggieApplication {
    public static void main(String args[]){
        SpringApplication.run(ReggieApplication.class);
        log.info("项目启动； ");
    }
}
