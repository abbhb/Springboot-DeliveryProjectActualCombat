package com.qc.ssm.ssmstudy.reggie.controller;


import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.service.CommonService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;
    @PostMapping("/uploadimage")
    public R<String> uploadImage(MultipartFile file){
        return commonService.uploadImage(file);

    }
}
