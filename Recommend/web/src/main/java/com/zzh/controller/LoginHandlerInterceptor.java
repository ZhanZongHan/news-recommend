package com.zzh.controller;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 通过session判断是否已经登陆
        Object loginUser = request.getSession().getAttribute("loginUser");

        if (loginUser == null) {
            // 没登陆，就跳转回登陆界面
            request.setAttribute("msg", "没有权限，请先登录！！！");
            request.getRequestDispatcher("/login").forward(request, response);
            return false;
        } else
            return true;
    }
}
