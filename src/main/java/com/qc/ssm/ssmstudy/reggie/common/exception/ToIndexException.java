package com.qc.ssm.ssmstudy.reggie.common.exception;

import javax.management.RuntimeErrorException;

/**
 * 自定义业务异常,此异常返回码为返回首页(2)
 */
public class ToIndexException extends RuntimeException {
    public ToIndexException(String message){
        super(message);
    }
}
