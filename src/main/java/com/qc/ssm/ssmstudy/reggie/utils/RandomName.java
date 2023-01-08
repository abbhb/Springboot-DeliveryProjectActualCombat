package com.qc.ssm.ssmstudy.reggie.utils;

import java.util.UUID;

public class RandomName {
    public static String getRandomName(String fileName){
        int index=fileName.lastIndexOf(".");
        String houzhui=fileName.substring(index);//获取后缀名
        String uuidFileName= UUID.randomUUID().toString().replace("-","")+houzhui;
        return uuidFileName;
    }

}
