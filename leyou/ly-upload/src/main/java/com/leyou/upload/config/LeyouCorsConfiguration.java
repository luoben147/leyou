package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * Cors跨域原理：（预检）请求到这里,并由此判断是否允许跨域访问,若允许才发起真正的请求访问
 */
@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){

        //1.初始化cors配置对象 添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了; * 代表所有域名都可以跨域访问
        config.addAllowedOrigin("http://manage.leyou.com");
        //2) 是否允许携带Cookie信息
        config.setAllowCredentials(true);
        //3) 允许的请求方式  *代表所有的请求方法 GET POST PUT DELETE...
        config.addAllowedMethod("*");
        //4）允许的头信息  *代表允许携带任何头信息
        config.addAllowedHeader("*");
        //5）此次预检有效时长,有效时长内无需预检直接跨域访问
        //config.setMaxAge(3600L);

        //2.初始化cors配置源对象，添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource=new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",config);

        return new CorsFilter(configurationSource);
    }

}
