package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.annotation.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.*;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.SetmealDishService;
import com.qc.ssm.ssmstudy.reggie.service.SetmealFlavorService;
import com.qc.ssm.ssmstudy.reggie.service.SetmealService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//@ResponseBody+@Controller
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    private final SetmealFlavorService setmealFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    public SetmealController(SetmealFlavorService setmealFlavorService) {
        this.setmealFlavorService = setmealFlavorService;
    }

    @GetMapping("/get")
    @NeedToken
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
    @NeedToken
    public R<String> addSetmeal(@RequestBody SetmealResult setmealResult){
        log.info(setmealResult.toString());
        return setmealService.addSetmeal(setmealResult);
    }

    @ApiOperation("put请求-修改套餐状态")
    @NeedToken
    @PutMapping("/put")
    public R<String> putMapping(@ApiParam("套餐的id") @RequestParam String ids,
                                @ApiParam("套餐的状态")@RequestParam String status) {
        return setmealService.updateStatus(ids,status);
    }

    /**
     * 单个参数
     * @param id
     * @return
     */
    @NeedToken
    @ApiOperation("删除套餐")
    @DeleteMapping("/del")
    public R<String> deleteMapping(
            @ApiParam(required = true, value = "主键id") @RequestParam String id) {
        return setmealService.deleteSetmeal(id);
    }

    @NeedToken
    @GetMapping("/get/dish")
    public R<List<DishResult>> getSetmeal(@ApiParam("套餐id")@RequestParam(value = "id",required = true) Long id){
        log.info("id = {}",id);
        return setmealDishService.getSetmealDish(id);//storeId必须不为空
    }

    /**
     * 获取口味情况
     * @return
     */
    @GetMapping ("/get/flavor")
    @NeedToken
    public R<List<SetmealFlavorResult>> getSetmealFlavor(@RequestParam(value = "id") String id){
        log.info(id);
        return setmealFlavorService.getSetmealFlavorResultBySetmealId(id);
    }

    /**
     * 更新套餐
     * @param setmealResult
     * @return
     */
    @PostMapping("/edit")
    @NeedToken
    public R<String> editSetmeal(@RequestBody SetmealResult setmealResult){
        log.info(setmealResult.toString());
        return setmealService.editSetmeal(setmealResult);
    }

    /**
     * 返回给前端
     * @param categoryId
     * @param storeId
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealResult>> getSetmealList(@RequestParam(value = "categoryId",required = true) Long categoryId,@RequestParam(value = "storeId",required = true) Long storeId){
        //在这里限制必须要有分类id
        return setmealService.getSetmealList(categoryId, storeId);
    }

    /**
     * 获取套餐详情
     * @param setmealId
     * @return
     */
    @GetMapping(value = "/dish/{setmealId}")
    public R<SetmealResult> getSetmealDetail(@PathVariable(value = "setmealId") String setmealId){

        return setmealService.getSetmealDetail(setmealId);
    }
}
