package com.qc.ssm.ssmstudy.reggie.service.impl;

import com.qc.ssm.ssmstudy.reggie.service.IStringRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IStringRedisServiceImpl implements IStringRedisService {

    private StringRedisTemplate redisTemplate;

    @Autowired
    public IStringRedisServiceImpl(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @Override
    public String getTokenId(String token) {
        return (String)redisTemplate.opsForValue().get(token);
    }

    @Override
    public void setTokenWithTime(String token, String value, Long time) {
        redisTemplate.opsForValue().set(token, value, time, TimeUnit.SECONDS);
    }

    @Override
    public void del(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public Long getTokenTTL(String tolen) {
        Long expire = redisTemplate.getExpire(tolen);
        return expire;
    }
}
