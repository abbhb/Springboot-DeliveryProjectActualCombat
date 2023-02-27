package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qc.ssm.ssmstudy.reggie.common.exception.CustomException;
import com.qc.ssm.ssmstudy.reggie.common.MyString;
import com.qc.ssm.ssmstudy.reggie.common.R;
import com.qc.ssm.ssmstudy.reggie.mapper.UserMapper;
import com.qc.ssm.ssmstudy.reggie.pojo.UserResult;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.User;
import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import com.qc.ssm.ssmstudy.reggie.service.UserService;
import com.qc.ssm.ssmstudy.reggie.utils.JWTUtil;
import com.qc.ssm.ssmstudy.reggie.utils.RandomName;
import com.qc.ssm.ssmstudy.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final IStringRedisService iStringRedisService;//这个是针对字符串的存储，若是存对象，请使用redisTemplate

    @Autowired
    public UserServiceImpl(IStringRedisService iStringRedisService) {
        this.iStringRedisService = iStringRedisService;
    }


    @Override
    public R<String> sendMsg(String phone) {
        //生成6位验证码
        Integer integer = ValidateCodeUtils.generateValidateCode(6);
        //把生成的验证码放入redis
        log.info("你的验证码为:"+integer);
        //每个手机号一个用途只能同时一个验证码
        //验证码5分钟过期,每个用户一分钟内不能连续请求(暂时只在前端实现)
        iStringRedisService.setTokenWithTime(MyString.pre_phone_redis+phone,String.valueOf(integer),300L);
        return R.success("获取成功!");
    }

    @Override
    @Transactional
    public R<UserResult> login(String phone, String code) {
        //在redis里查找phone所对应的key-value
        String value = iStringRedisService.getValue(MyString.pre_phone_redis + phone);
        if (!StringUtils.isNotEmpty(value)){
            return R.error("验证码错误");
        }
        if (value.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = super.getOne(queryWrapper);
            if (user==null){
                user = new User();
//                log.info("1");

                user.setName(RandomName.getUUID());
                user.setSex("男");
                user.setId(null);
                user.setPhone(phone);
                user.setIsDeleted(0);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                user.setStatus(1);//新建的账号都是启用状态

                boolean save = super.save(user);
                if (!save){
                    throw new CustomException("业务异常");
                }
            }
            UserResult userResult = new UserResult();
            userResult.setName(user.getName());
            userResult.setId(String.valueOf(user.getId()));
            userResult.setAvatar(user.getAvatar());
            userResult.setSex(user.getSex());
            userResult.setPhone(user.getPhone());
            userResult.setStatus(user.getStatus());
            userResult.setIdNumber(user.getIdNumber());
            userResult.setCreateTime(user.getCreateTime());
            userResult.setUpdateTime(user.getUpdateTime());
            //生成token
            String uuid = RandomName.getUUID();
            String token = JWTUtil.getToken(String.valueOf(user.getId()), "user", uuid);
            userResult.setToken(token);
            //token写入redis,给用户4小时有效期，操作给用户刷新
            iStringRedisService.setTokenWithTime(MyString.pre_user_redis+uuid,String.valueOf(user.getId()),4*3600L);
            //返回token
            return R.success(userResult);
        }
        return R.error("业务异常");
    }
}
