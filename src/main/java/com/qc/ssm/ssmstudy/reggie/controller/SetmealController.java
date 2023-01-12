package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishAndCategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.SetmealResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController//@ResponseBody+@Controller
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/get")
    public R<PageData<SetmealResult>> getSetmeal(@RequestParam(value = "pageNum",required = false) Integer pageNum, @RequestParam(value = "pageSize",required = false) Integer pageSize, @RequestParam(value = "storeId",required = true) Long storeId, @RequestParam(value = "name",required = false) String name){
        log.info(name);
        return setmealService.getSetmeal(pageNum,pageSize,storeId,name);//storeId必须不为空
    }

    /**
     * 新增套餐
     * @param setmealResult
     * @return
     */
    @PostMapping("/add")
    public R<String> addSetmeal(@RequestBody SetmealResult setmealResult){
        log.info(setmealResult.toString());
        return setmealService.addSetmeal(setmealResult);
    }

}
