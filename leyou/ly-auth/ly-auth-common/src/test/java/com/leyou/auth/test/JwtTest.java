package com.leyou.auth.test;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utisl.JwtUtils;
import com.leyou.common.utisl.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "D:\\worker\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\worker\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3NDIzODI4N30.ZVG-TtuP8-mYV2wNZAOiPWtBaJxPO0zf7HoukV5QiSWyy5ldRK9tOBq9y1LDqIbEnzuL0KY9dyI8lIUL0ir49j7M2trkLeAgGOiMK-5ynWCQMvxAAJ4VSbXjV_gvRSH5fwu9rHiNl-Mg9RcXkRRwh2kkK1vnQ_BxuMJ8PWGb4Kw";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}