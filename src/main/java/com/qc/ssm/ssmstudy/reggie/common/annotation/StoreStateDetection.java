package com.qc.ssm.ssmstudy.reggie.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//作用域是方法
@Target(ElementType.METHOD)
//注解不仅保存在calss文件中，jvm加载class文件之后，仍然存在
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreStateDetection {
}
