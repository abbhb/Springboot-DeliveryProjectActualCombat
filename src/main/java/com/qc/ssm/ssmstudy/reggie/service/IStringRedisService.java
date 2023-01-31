package com.qc.ssm.ssmstudy.reggie.service;

public interface IStringRedisService {
//    String getTokenId(String token);

    void setTokenWithTime(String token,String value,Long time);

    void del(String token);

    Long getTokenTTL(String uuid);
    String getValue(String key);
}
