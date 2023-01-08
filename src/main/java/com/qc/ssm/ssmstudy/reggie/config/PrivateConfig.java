package com.qc.ssm.ssmstudy.reggie.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//不上传git
@Data
@Configuration
@ConfigurationProperties(prefix = "privateconfig")
public class PrivateConfig {

    //公司名
    private String companyName;
}
