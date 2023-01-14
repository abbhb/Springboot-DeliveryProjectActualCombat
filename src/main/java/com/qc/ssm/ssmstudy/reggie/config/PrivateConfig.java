package com.qc.ssm.ssmstudy.reggie.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//不上传git
@Data
@Configuration
@Component
@ConfigurationProperties(prefix = "privateconfig")
public class PrivateConfig {

    //公司名
    private String companyName;

    private String secret_key;
}
