package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishAndCategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品相关的接口，包括口味管理
 */
@RestController//@ResponseBody+@Controller
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @PostMapping("/add")
    public R<String> addDish(@RequestBody DishResult dishResult){
        log.info(dishResult.toString());
        return dishService.addDish(dishResult);
    }

    @GetMapping ("/get")
    public R<PageData<DishAndCategoryResult>> getDish(@RequestParam(value = "pageNum",required = false) Integer pageNum,@RequestParam(value = "pageSize",required = false) Integer pageSize,@RequestParam(value = "storeId",required = true) Long storeId,@RequestParam(value = "name",required = false) String name){
        log.info(name);
        return dishService.getDish(pageNum,pageSize,storeId,name);
    }

}
