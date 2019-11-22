package com.leyou.cart.interceptor;


import com.leyou.cart.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utisl.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    //存放登录用户信息的请求线程共享容器,放行的请求可以在Controller层取得用户信息
    private static ThreadLocal<UserInfo> tl=new ThreadLocal();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        //解析token,获取用户信息
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

        if(userInfo==null){
            //拦截
            return false;
        }
        //userInfo放入线程局部变量
        tl.set(userInfo);
        //放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        /**
         * 请求线程结束时清空线程共享容器
         */
        tl.remove();
    }

    /**
     * 提供给其他层获取线程容器内容的方法
     */
    public static UserInfo getUserInfo() {
        return tl.get();
    }

}
