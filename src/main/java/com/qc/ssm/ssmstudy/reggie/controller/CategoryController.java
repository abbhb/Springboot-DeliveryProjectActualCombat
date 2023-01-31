package com.qc.ssm.ssmstudy.reggie.controller;

import com.qc.ssm.ssmstudy.reggie.common.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.CategoryResult;
import com.qc.ssm.ssmstudy.reggie.pojo.ValueLabelResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Category;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.PageData;
import com.qc.ssm.ssmstudy.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController//@ResponseBody+@Controller
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    /**
     * 新建
     * {"id":"1397844263642378253","type":"1","name":"菜品类型测试","sort":"13","storeId":"1609470007786618882"}
     * {"type":"1","name":"菜品类型测试","sort":"13","storeId":"1609470007786618882"}
     * ID可以由雪花算法生成，也可以指定，但是建议生成，方便后续分表
     * @param category
     * @return
     */
    @PostMapping("/save")
    @NeedToken
    public R<String> saveCategory(@RequestBody Category category){
        //springboot十分强大，居然能把字符串映射到对象并且转换成指定类型，太厉害了
        log.info("category = {}",category);
        return categoryService.saveCategory(category);
    }


    /**
     * 更新
     * {"id":"1397844263642378253","type":"1","name":"菜品类型测试","sort":"13","storeId":"1609470007786618882"}
     * {"type":"1","name":"菜品类型测试","sort":"13","storeId":"1609470007786618882"}
     * ID可以由雪花算法生成，也可以指定，但是建议生成，方便后续分表
     * @param category
     * @return
     */
    @NeedToken
    @PostMapping("/updata")
    public R<String> updataCategory(@RequestBody Category category){
        //springboot十分强大，居然能把字符串映射到对象并且转换成指定类型，太厉害了
        log.info("category = {}",category);
        return categoryService.updataCategory(category);
    }


    /**
     * 获取分页数据:套餐和分类
     * {"pageNum":"1","pageSize":"10"}
     * @param category
     * @return
     */
    @NeedToken
    @PostMapping("/getcategorypage")
    public R<PageData<CategoryResult>> getCategoryPage(@RequestBody Map<String, Object> category){
        //springboot十分强大，居然能把字符串映射到对象并且转换成指定类型，太厉害了
        log.info("category = {}",category);
        String pageNum = (String) category.get("pageNum");
        String pageSize = (String) category.get("pageSize");
        String storeId = (String) category.get("storeId");

        return categoryService.getCategoryPage(pageNum,pageSize,storeId);
    }

    /**
     * 删除分类
     * @return
     */
    @NeedToken
    @PostMapping("/deletecategory")
    public R<String> deleteCategory(@RequestBody Map<String, Object> category){
        String id = (String) category.get("id");//唯一的，不用再去判断门店了
        return categoryService.deleteCategory(id);
    }

    /**
     * 获取分类列表
     * @return
     */
    @NeedToken
    @PostMapping("/getcategorylablevaluelist")
    public R<List<ValueLabelResult>> getCategoryLableValueList(@RequestBody Map<String, Object> category){
        String storeId = (String) category.get("storeId");
        String type = (String) category.get("type");
        return categoryService.getCategoryLableValueList(storeId,type);
    }


    @GetMapping("/list")
    public R<List<CategoryResult>> getCategoryList(@RequestParam("storeId") Long storeId){
        return categoryService.getCategoryList(storeId);
    }
}
