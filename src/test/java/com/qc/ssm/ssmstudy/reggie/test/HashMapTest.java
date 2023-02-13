package com.qc.ssm.ssmstudy.reggie.test;

import com.qc.ssm.ssmstudy.reggie.utils.MessageHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HashMapTest {
    @Test
    public void testA(){
        MessageHashMap.addMessage("123123123","123123123");
        MessageHashMap.addMessage("123123123","1nhr3242123");
        MessageHashMap.addMessage("123123123","3fw13423");
        MessageHashMap.addMessage("123123123","ggg123123");
        List<String> message = MessageHashMap.getMessage("123123123");
        message.forEach(System.out::println);
    }
}
