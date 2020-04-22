package com.zzh.controller;


import com.zzh.pojo.User;
import com.zzh.pojo.UserPush;
import com.zzh.service.UserService;
import com.zzh.tools.MySqlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model,
            HttpSession session) {
        // @RequestParam对应这表单的name属性

        // 判断用户是否存在
        User user = userService.isLegalUser(username, password);
        if (user != null) {
            // 登陆成功，就设置一个session
            session.setAttribute("loginUser", username);

            // 重定向到index.html，地址名也会用index.html，不能有空格
            if (user.getAuthority() == 1)
                return "redirect:/index.html";
            else {
                // 初始化普通用户
                UserPush loginUserPush = (UserPush) session.getAttribute("loginUserPush");
                if (loginUserPush == null) {

                    loginUserPush = userService.initLoginUser(user, 1, MySqlProperties.NEWS_PAGE_NUM);
                    session.setAttribute("loginUserPush", loginUserPush);
                }
//                model.addAttribute("loginUserPush", loginUserPush);
                return "pre-index";
            }

        } else {
            model.addAttribute("msg", "用户名或密码错误！");
            return "login";
        }
    }

    @RequestMapping("/user/logout")
    public String logout(HttpSession session){
        session.setAttribute("loginUser", null);
        return "login";
    }
}
