package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.DishAndCategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishFlavorResult;
import com.qc.ssm.ssmstudy.reggie.pojo.DishResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.DishFlavorService;
import com.qc.ssm.ssmstudy.reggie.service.DishService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品相关的接口，包括口味管理
 */
@RestController//@ResponseBody+@Controller
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @NeedToken
    @PostMapping("/add")
    public R<String> addDish(@RequestBody DishResult dishResult){
        log.info(dishResult.toString());
        return dishService.addDish(dishResult);
    }

    @NeedToken
    @PostMapping("/edit")
    public R<String> editDish(@RequestBody DishResult dishResult){
        log.info(dishResult.toString());
        return dishService.editDish(dishResult);
    }

    @NeedToken
    @GetMapping ("/get")
    public R<PageData<DishAndCategoryResult>> getDish(@RequestParam(value = "pageNum",required = false) Integer pageNum,@RequestParam(value = "pageSize",required = false) Integer pageSize,@RequestParam(value = "storeId",required = true) Long storeId,@RequestParam(value = "name",required = false) String name){
        log.info(name);
        return dishService.getDish(pageNum,pageSize,storeId,name);
    }

    @ApiOperation("put请求-单个参数RequestParam、文件上传")
    @NeedToken
    @PutMapping("/put")
    public R<String> putMapping(@RequestParam String id,
                                 @RequestParam String status) {
        return dishService.updateStatus(id,status);
    }

    /**
     * 单个参数
     * @param id
     * @return
     */
    @DeleteMapping("/del")
    @NeedToken
    public R<String> deleteMapping(
            @ApiParam(required = false, value = "主键id") @RequestParam String id) {
        return dishService.deleteDishAndFlavor(id);
    }

    /**
     * 获取口味情况
     * @return
     */
    @GetMapping ("/get/flavor")
    @NeedToken
    public R<List<DishFlavorResult>> getDishFlavor(@RequestParam(value = "id") String id){
        log.info(id);
        return dishFlavorService.getDishFlavorResultByDishId(id);
    }

    @GetMapping ("/get/listbycategory")
    @NeedToken
    public R<List<DishResult>> getDishListByCategoryId(@RequestParam(value = "categoryId",required = false) Long categoryId,@RequestParam(value = "storeId",required = true) Long storeId,@RequestParam(value = "name",required = false) String name){
        return dishService.getDishListByCategoryId(categoryId,storeId,name);
    }


    /**
     * 返回给前端
     * @param categoryId
     * @param storeId
     * @return
     */
    @GetMapping("list")
    public R<List<DishResult>> getDishList(@RequestParam(value = "categoryId",required = true) Long categoryId,@RequestParam(value = "storeId",required = true) Long storeId){
        //在这里限制必须要有分类id
        return dishService.getDishList(categoryId, storeId);
    }

}
