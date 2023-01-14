package com.qc.ssm.ssmstudy.reggie.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.qc.ssm.ssmstudy.reggie.config.PrivateConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    private final static String secret_key = "reggit20230113.FfG!D3a2AcfrF.2u0C1";
    /**
     * 生成加密后的token
     * @param id ID字符串
     * @return 加密后的token
     */
    public static String getToken(String id, String permissions,String uuid) {
        String token = null;
        try {//60L *
//            Date expiresAt = new Date(System.currentTimeMillis()+ 1L*60L * 1000L);//token 60分钟内必须刷新，后期加个刷新令牌，刷新令牌放redis里
            token = JWT.create().withIssuer("auth0").withClaim("id", id).withClaim("permissions", permissions).withClaim("uuid", uuid)
                    // 使用了HMAC256加密算法。
                    // mysecret是用来加密数字签名的密钥。
                    .sign(Algorithm.HMAC256(secret_key));
        } catch (JWTCreationException exception) {
            // Invalid Signing configuration / Couldn't convert Claims.
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 解密
     * @author 张超
     *
     */
    /**
     * 先验证token是否被伪造，然后解码token。
     * @param token 字符串token
     * @return 解密后的DecodedJWT对象，可以读取token中的数据。
     */
    public static DecodedJWT deToken(final String token) {
        DecodedJWT jwt = null;
        try {
            // 使用了HMAC256加密算法。
            // mysecret是用来加密数字签名的密钥。
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret_key)).withIssuer("auth0").build();// Reusable
            // verifier
            // instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            // Invalid signature/claims
            exception.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jwt;
    }

}
