package com.qc.ssm.ssmstudy.reggie.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTUtil {
    public static String createToken(Long id){
        String sign = JWT.create()
                .withClaim("userid", id) // payload
                .sign(Algorithm.HMAC256("$J#F@f@G!D")); // 签名
        return sign;
    }
    public static Long getTokenId(String token){
        DecodedJWT decode = JWT.decode(token);
        Claim userid = decode.getClaim("userid");
        return userid.asLong();
    }
}
