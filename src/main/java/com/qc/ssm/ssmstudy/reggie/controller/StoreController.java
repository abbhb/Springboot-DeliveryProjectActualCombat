package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.dto.EmployeeResult;
import com.qc.ssm.ssmstudy.reggie.dto.StoreResult;
import com.qc.ssm.ssmstudy.reggie.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping("/addmendian")
    public R<StoreResult> addStore(@RequestBody Map<String, Object> store){
        String id = (String) store.get("id");//因为雪花算法，所以ID来回传递使用字符串,传回Service前转会Long
        Long idl = null;
        if (id!=null){
            idl = Long.valueOf(id);
        }
        String name = (String) store.get("name");
        String introduction = (String) store.get("introduction");
        Integer status = (Integer) store.get("status");
        return storeService.addStore(idl,name,introduction,status);
    }
}
