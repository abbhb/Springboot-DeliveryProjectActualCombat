package com.qc.ssm.ssmstudy.reggie.config;

import com.qc.ssm.ssmstudy.reggie.common.JwtTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenInterceptor jwtTokenInterceptor;
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");//此处classpath:/front/必须得在末尾加/，否则无法访问
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截规则
        InterceptorRegistration ir = registry.addInterceptor(jwtTokenInterceptor);
        // 拦截路径，员工请求的路径都拦截
        ir.addPathPatterns("/employee/**");
        ir.addPathPatterns("/store/**");
        // 不拦截路径，如：注册、登录、忘记密码等
        ir.excludePathPatterns("/employee/login","/employee/logout");//可以直接逗号往后加
    }

}
