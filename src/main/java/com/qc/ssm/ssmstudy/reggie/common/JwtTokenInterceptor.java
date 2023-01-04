package com.qc.ssm.ssmstudy.reggie.common;

import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import com.qc.ssm.ssmstudy.reggie.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
public class JwtTokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IStringRedisService iStringRedisService;//这个是针对字符串的存储，若是存对象，请使用redisTemplate
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在拦截器中，如果请求为OPTIONS请求，则返回true，表示可以正常访问，然后就会收到真正的GET/POST请求
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            System.out.println("OPTIONS请求，放行");
            return true;
        }
        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (method.getAnnotation(NeedToken.class) != null){
            //token校验

            String authorization =  request.getHeader("Authorization");

            String id =  request.getHeader("userid");//此处id为字符串

//            System.out.println("authorization = " + authorization);
//            if(redisOperator)
            if (authorization==null){
                response.setStatus(Code.DEL_TOKEN);
                return false;
            }
            if (id == null){
                response.setStatus(Code.DEL_TOKEN);
                return false;
            }
            System.out.println("authorization = " + authorization);//将id直接传回

            Long tokenTTL = iStringRedisService.getTokenTTL(authorization);

            if (tokenTTL==null){
                log.info("tokenTTL==null");
                response.setStatus(Code.DEL_TOKEN);
                return false;
            }else {
                if (tokenTTL.intValue()!=-2){
                    if (tokenTTL.intValue()<=1500){
                        //一小时内如果访问过需要token的接口且token剩余时间小于1500s的话重置token过期时间为3600s
                        iStringRedisService.setTokenWithTime(authorization,id,3600L);
                    }
                }else {
                    log.info("tokenTTL==-2");
                    response.setStatus(Code.DEL_TOKEN);
                    return false;
                }
                if (id!=null||authorization!=null){
                    //token存在，放行
                }else {
                    log.info("token拦截器:1");
                    response.setStatus(Code.DEL_TOKEN);
                    return false;
                }
            }
            return true;
        }
        return true;


    }

}
