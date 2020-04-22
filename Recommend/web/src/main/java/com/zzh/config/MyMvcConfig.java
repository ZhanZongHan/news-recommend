package com.zzh.config;

import com.zzh.controller.LoginHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 跳转首页
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        // 跳转登陆界面
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/login.html").setViewName("login");
        // 404页面
        registry.addViewController("/error").setViewName("error");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        // 配置拦截的路径以及放行的路径
        registry.addInterceptor(new LoginHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/login.html", "/", "/user/login", "/css/*", "/js/*", "/fonts/*", "/images/*");
    }
}
