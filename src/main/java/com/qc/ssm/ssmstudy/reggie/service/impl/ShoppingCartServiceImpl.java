package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.ShoppingCartMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.ShoppingCartResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Dish;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.Setmeal;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.ShoppingCart;
import com.qc.ssm.ssmstudy.reggie.service.DishService;
import com.qc.ssm.ssmstudy.reggie.service.SetmealService;
import com.qc.ssm.ssmstudy.reggie.service.ShoppingCartService;
import com.qc.ssm.ssmstudy.reggie.service.WebSocketServer;
import com.qc.ssm.ssmstudy.reggie.utils.MessageHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    private final DishService dishService;
    private final SetmealService setmealService;

    @Autowired
    public ShoppingCartServiceImpl(DishService dishService, SetmealService setmealService) {
        this.dishService = dishService;
        this.setmealService = setmealService;
    }

    /**
     * 暂时不加事物
     * @param userId
     * @param storeId
     * @return
     */
    @Override
    public R<List<ShoppingCartResult>> getList(Long userId, Long storeId) {
        if (userId==null){
            return R.error("null");
        }
        if (storeId==null){
            return R.error("null");
        }
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ShoppingCart::getCreateTime,ShoppingCart::getDishFlavor,ShoppingCart::getDishId,ShoppingCart::getId,ShoppingCart::getNumber,ShoppingCart::getSetmealId,ShoppingCart::getType,ShoppingCart::getVersion,ShoppingCart::getUserId);
        queryWrapper.eq(ShoppingCart::getUserId,userId).eq(ShoppingCart::getStoreId,storeId);
        //原始购物车数据
        List<ShoppingCart> list = super.list(queryWrapper);
        //返回购物车数据
        List<ShoppingCartResult> shoppingCartResults = new ArrayList<>();
        String promptToBeSent = "抱歉,您的购物车内存在被下架的商品[";
        boolean promptToBeSentIsSend = false;
        String versionNumberReminder = "提醒:您购物车里部分商品[";
        boolean versionNumberReminderIsSend = false;
        for (ShoppingCart item:
             list) {
            if (item.getType()==1){
                //dish
                LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishLambdaQueryWrapper.eq(Dish::getId,item.getDishId());
                //获取原始dish数据
                Dish dish = dishService.getOne(dishLambdaQueryWrapper);
                //判断原始dish没有被下架和删除
                if (dish.getStatus()==0||dish.getIsDeleted()==1){
                    super.removeById(item.getId());
                    promptToBeSent = promptToBeSent + dish.getName() + ",";
                    promptToBeSentIsSend = true;
//                    MessageHashMap.addMessage(String.valueOf(item.getUserId()),str);
                } else {
                    //没有被下架或者删除
                    //判断版本号是否一致,不一致提醒用户,因为数据都是最新的
                    log.info("itemversion = {},dishversion = {}",item.getVersion(),dish.getVersion());
                    if (!Objects.equals(item.getVersion(), dish.getVersion())){
                        versionNumberReminder = versionNumberReminder + dish.getName()+ ",";
                        log.info("item = {}",item);
                        log.info("dish = {}",dish);
                        versionNumberReminderIsSend = true;
                        //顺便更正版本号,保证消息只推送一次
                        LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                        shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getId,item.getId());

                        shoppingCartLambdaUpdateWrapper.set(ShoppingCart::getVersion,dish.getVersion());
                        //更新版本号
                        super.update(shoppingCartLambdaUpdateWrapper);
                    }
                    ShoppingCartResult shoppingCartResult = new ShoppingCartResult();
                    shoppingCartResult.setId(String.valueOf(item.getId()));
                    shoppingCartResult.setNumber(item.getNumber());
                    shoppingCartResult.setUserId(String.valueOf(item.getUserId()));
                    shoppingCartResult.setVersion(item.getVersion());
                    shoppingCartResult.setName(dish.getName());
                    shoppingCartResult.setCreateTime(item.getCreateTime());
                    shoppingCartResult.setAmount(String.valueOf(dish.getPrice()));
                    shoppingCartResult.setImage(dish.getImage());
                    shoppingCartResult.setDishFlavor(item.getDishFlavor());
                    shoppingCartResult.setDishId(String.valueOf(item.getDishId()));
                    shoppingCartResult.setType(1);
                    //返回购物车数据
                    shoppingCartResults.add(shoppingCartResult);
                }
            }else if (item.getType()==2){
                //setmeal
                LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
                setmealLambdaQueryWrapper.eq(Setmeal::getId,item.getSetmealId());
                //获取原始dish数据
                Setmeal setmeal = setmealService.getOne(setmealLambdaQueryWrapper);
                //判断原始dish没有被下架和删除
                if (setmeal.getStatus()==0||setmeal.getIsDeleted()==1){
                    super.removeById(item.getId());
                    promptToBeSent = promptToBeSent + setmeal.getName() + ",";
                    promptToBeSentIsSend = true;
//                    MessageHashMap.addMessage(String.valueOf(item.getUserId()),str);
                } else {
                    //没有被下架或者删除
                    //判断版本号是否一致,不一致提醒用户,因为数据都是最新的
                    if (!Objects.equals(item.getVersion(), setmeal.getVersion())){
                        versionNumberReminder = versionNumberReminder + setmeal.getName()+ ",";
                        versionNumberReminderIsSend = true;
                        //顺便更正版本号,保证消息只推送一次
                        LambdaUpdateWrapper<ShoppingCart> shoppingCartLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                        shoppingCartLambdaUpdateWrapper.eq(ShoppingCart::getId,item.getId());
                        shoppingCartLambdaUpdateWrapper.set(ShoppingCart::getVersion,setmeal.getVersion());
                        //更新版本号
                        super.update(shoppingCartLambdaUpdateWrapper);
                    }
                    ShoppingCartResult shoppingCartResult = new ShoppingCartResult();
                    shoppingCartResult.setId(String.valueOf(item.getId()));
                    shoppingCartResult.setNumber(item.getNumber());
                    shoppingCartResult.setUserId(String.valueOf(item.getUserId()));
                    shoppingCartResult.setVersion(item.getVersion());
                    shoppingCartResult.setName(setmeal.getName());
                    shoppingCartResult.setCreateTime(item.getCreateTime());
                    shoppingCartResult.setAmount(String.valueOf(setmeal.getPrice()));
                    shoppingCartResult.setImage(setmeal.getImage());
                    shoppingCartResult.setDishFlavor(item.getDishFlavor());
                    shoppingCartResult.setDishId(String.valueOf(item.getDishId()));
                    shoppingCartResult.setType(2);
                    //返回购物车数据
                    shoppingCartResults.add(shoppingCartResult);
                }
            }
        }
        //删除或者停售移除的消息提醒
        if (promptToBeSentIsSend){
            promptToBeSent = promptToBeSent.substring(0,promptToBeSent.length()-1) + "] 我们已经帮你您从购物车中移除这些菜品了,谢谢理解!\n";
            MessageHashMap.addMessage(String.valueOf(userId),promptToBeSent);
        }

        //版本号更改的消息提醒
        if (versionNumberReminderIsSend){
            versionNumberReminder = versionNumberReminder.substring(0,versionNumberReminder.length()-1) +"] 的原配置已经发生了改变,这一改变可能来自商家调整,请关注!\n";
            MessageHashMap.addMessage(String.valueOf(userId),versionNumberReminder);
        }
        if (MessageHashMap.equals(String.valueOf(userId))){
            int nw = 1;
            HashSet<String> sids = new HashSet<>();
            sids.add(String.valueOf(userId));
            String messages = "";
            for (String assa:
                    (List<String>)MessageHashMap.getMessage(String.valueOf(userId))) {
                messages = messages + " [" + nw + "] " +assa;
                nw++;
            }
            try {
                WebSocketServer.sendMessage(messages,sids);//每次发完消息得清掉消息
            }catch (Exception e){
                e.printStackTrace();//捕获异常
            }
        }
        return R.success(shoppingCartResults);
    }

    @Override
    public R<String> clean(Long userId, Long storeId) {
        if (userId==null){
            return R.error("null");
        }
        if (storeId==null){
            return R.error("null");
        }
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getStoreId,storeId);
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        boolean remove = super.remove(shoppingCartLambdaQueryWrapper);
        if (remove){
            return R.success("清空成功!");
        }
        return R.error("没有清空!");
    }
}
