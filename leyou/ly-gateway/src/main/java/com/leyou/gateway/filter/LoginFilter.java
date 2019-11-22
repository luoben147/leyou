package com.leyou.gateway.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utisl.JwtUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * zuul网关转发请求前先校验cookie中是否有token
 * 若有token则使用jwt工具类校验,只有校验成功(已登录)才转发到微服务,否则拦截返回403
 */
@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        //前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        //过滤器顺序
        return 10;
    }

    /**
     * 是否过滤
     */
    @Override
    public boolean shouldFilter() {
        //获取白名单
        List<String> allowPaths = filterProperties.getAllowPaths();

        //初始化zuul运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = context.getRequest();
        //获取请求路径
        String url = request.getRequestURL().toString();

        for (String allowPath : allowPaths) {
            if(StringUtils.contains(url,allowPath)){
                return false;
            }
        }

        return true;
    }

    /**
     * 过滤逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {

        //初始化zuul运行上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = context.getRequest();

        //从cookie取得token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        if(StringUtils.isBlank(token)){
            //拦截请求
            context.setSendZuulResponse(false);
            //访问拒绝状态码
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        try {
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();

            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null; //不管返回什么都是默认放行请求
    }
}
