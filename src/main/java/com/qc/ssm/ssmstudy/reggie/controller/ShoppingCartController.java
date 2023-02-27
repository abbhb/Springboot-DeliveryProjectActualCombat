package com.qc.ssm.ssmstudy.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qc.ssm.ssmstudy.reggie.common.annotation.NeedToken;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.pojo.ShoppingCartResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.ShoppingCart;
import com.qc.ssm.ssmstudy.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    @NeedToken
    public R<String> add(@RequestHeader(value="userid", required = true)String userid, @RequestBody ShoppingCart shoppingCart) {
//        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("shoppingCart:{},userid:{}", shoppingCart,userid);
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("userid为空");
        }
        shoppingCart.setUserId(Long.valueOf(userid));
        shoppingCart.setCreateTime(LocalDateTime.now());
        boolean save = shoppingCartService.save(shoppingCart);
        if (!save){
            return R.error("保存失败");
        }
        return R.success("保存成功");
    }
    /**
     * 获取
     * @param userid 用户id
     * @param storeId 商店id(区分是哪个门店的购物车)
     * 每次请求顺便对比version版本和菜品套餐库的数据版本，不一致就推送消息
     */
    @GetMapping("/list")
    @NeedToken
    public R<List<ShoppingCartResult>> get(@RequestHeader(value="userid", required = true)String userid,String storeId){
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("错误");
        }
        if (!StringUtils.isNotEmpty(storeId)) {
            return R.error("err");
        }
        log.info("storeId = {}",storeId);
        return shoppingCartService.getList(Long.valueOf(userid),Long.valueOf(storeId));
    }
    @DeleteMapping("/clean")
    @NeedToken
    public R<String> clean(@RequestHeader(value="userid", required = true)String userid,String storeId){
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("错误");
        }
        if (!StringUtils.isNotEmpty(storeId)) {
            return R.error("err");
        }
        log.info("storeId = {}",storeId);
        return shoppingCartService.clean(Long.valueOf(userid),Long.valueOf(storeId));
    }

    @PostMapping("/numberUpdate")
    @NeedToken
    public R<String> numberUpdate(@RequestHeader(value="userid", required = true)String userid,@RequestBody ShoppingCart shoppingCart){
        if (!StringUtils.isNotEmpty(userid)){
            return R.error("参数异常");
        }
        if (shoppingCart.getId()==null){
            return R.error("参数异常");
        }
        if (shoppingCart.getNumber()==null){
            return R.error("参数异常");
        }
        if (shoppingCart.getNumber()<1||shoppingCart.getNumber()>99){
            return R.error("数据不合理");
        }
        LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        shoppingCartLambdaUpdateWrapper.set(ShoppingCart::getNumber,shoppingCart.getNumber());
        shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getId,shoppingCart.getId());
        boolean update = shoppingCartService.update(shoppingCartLambdaUpdateWrapper);
        if (update){
            return R.success("success");
        }
        return R.error("error");
    }
}
