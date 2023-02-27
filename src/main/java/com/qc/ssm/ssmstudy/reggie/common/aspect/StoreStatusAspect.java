package com.qc.ssm.ssmstudy.reggie.common.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qc.ssm.ssmstudy.reggie.common.exception.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.annotation.StoreStateDetection;
import com.qc.ssm.ssmstudy.reggie.common.exception.ToIndexException;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Store;
import com.qc.ssm.ssmstudy.reggie.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.ClassClassPath;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class StoreStatusAspect {
    private final StoreService storeService;

    private static String[] types = {"java.lang.Integer", "java.lang.Double",
            "java.lang.Float", "java.lang.Long", "java.lang.Short",
            "java.lang.Byte", "java.lang.Boolean", "java.lang.Char",
            "java.lang.String", "int", "double", "long", "short", "byte",
            "boolean", "char", "float"};


    @Autowired
    public StoreStatusAspect(StoreService storeService) {
        this.storeService = storeService;
    }

    @Before("within(com.qc.ssm.ssmstudy.reggie.service..*) && @annotation(storeStateDetection)")
    public void storeStatusDetection(JoinPoint joinPoint, StoreStateDetection storeStateDetection) {
        Map<String, Object> fieldsNameValueMap = getFieldsNameValueMap(joinPoint);
        if (ObjectUtils.isEmpty(fieldsNameValueMap.get("storeId"))){
            throw new CustomException("aspect-->storeId-->err-->o");
        }
        Long storeId = (Long) fieldsNameValueMap.get("storeId");
        if (storeId==null){
            throw new CustomException("aspect-->storeId-->err-->l");
        }
        //判断此刻门店是否为关闭状态
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Store::getStoreId,storeId);
        lambdaQueryWrapper.eq(Store::getStoreStatus,1);//营业中,会默认查询没被删除的数据
        Store store = storeService.getOne(lambdaQueryWrapper);
        if (store==null){
            throw new ToIndexException("门店已打烊!");
        }
    }
    private Map<String,Object> getFieldsNameValueMap(JoinPoint joinPoint) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = methodSignature.getParameterNames();
            Object[] args = joinPoint.getArgs();
            String classType = joinPoint.getTarget().getClass().getName();
            Class<?> clazz = Class.forName(classType);
            String clazzName = clazz.getName();
            String methodName = joinPoint.getSignature().getName(); //获取方法名称
            Map<String,Object > map=new HashMap<String,Object>();
            ClassPool pool = ClassPool.getDefault();
            ClassClassPath classPath = new ClassClassPath(this.getClass());
            pool.insertClassPath(classPath);
            CtClass cc = pool.get(clazzName);
            CtMethod cm = cc.getDeclaredMethod(methodName);
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                throw new RuntimeException();
            }
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            int parame = 0;
            for (int i = 0; i < cm.getParameterTypes().length; i++){
                for (String t : types){
                    if (t.equals(args[i].getClass().getName())){
                        map.put( parameterNames[parame],args[i]);//paramNames即参数名
                        parame += 1;
                    }else {
                        Field[] fields = args[i].getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            map.put(field.getName(), field.get(args[i]));
                        }
                    }
                }
            }
            return map;
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }

    }
}
