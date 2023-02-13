package com.qc.ssm.ssmstudy.reggie.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class MessageHashMap {
    public static HashMap<String, Object> messageHashMap = new HashMap<>();

    public static void addMessage(String uid,String needAddMessage){
        List<String> newV = null;
        if (messageHashMap.containsKey(uid)) {
            List<String> oldV = (List<String>) messageHashMap.get(uid);
            oldV.add(needAddMessage);
            newV = oldV;
        }else {
            newV = new ArrayList<>();
            newV.add(needAddMessage);
        }
        messageHashMap.put(uid, newV);
    }

    public static boolean equals(String uid){
        if (messageHashMap.containsKey(uid)){
            return true;
        }
        return false;
    }

    public static List<String>getMessage(String uid){
        if (messageHashMap.containsKey(uid)){
            return (List<String>) messageHashMap.get(uid);
        }
        return null;
    }

    public static void cleanMessage(String sid) {
        messageHashMap.remove(sid);
    }
}
