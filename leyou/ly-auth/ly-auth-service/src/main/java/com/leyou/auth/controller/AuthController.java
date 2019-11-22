package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utisl.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录授权
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username") String username,
                                         @RequestParam("password") String password,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        //1.登录校验
        String token = authService.accredit(username, password);
        if (StringUtils.isBlank(token)) {
            //401 身份验证未通过
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //2.将token写入cookie，并指定httpOnly为true，防止通过js获取和修改
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);

        return ResponseEntity.ok(null);
    }

    /**
     * 用户验证
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(
            @CookieValue("LY_TOKEN") String token,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            //jwt工具类使用公钥解析jwt
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //刷新jwt中的有效时间
            token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            //刷新cookie中的有效时间
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
