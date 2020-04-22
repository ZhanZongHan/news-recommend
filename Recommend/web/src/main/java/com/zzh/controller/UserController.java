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
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/user/user_index")
    public String getUsersByIndex(Model model) {
        List<User> users = userService.getUsersByPage(1, MySqlProperties.USER_PAGE_NUM);
        model.addAttribute("curPage", 1);
        model.addAttribute("users", users);
        model.addAttribute("nums", users.size());
        model.addAttribute("hasNextPage", 1);
        return "user/user_lists";
    }

    @RequestMapping("/user/user_lists.html")
    public String getUsersByPage(
            @RequestParam("nextPage") Integer nextPage,
            Model model) {
        List<User> users = userService.getUsersByPage(nextPage, MySqlProperties.USER_PAGE_NUM);
        model.addAttribute("curPage", nextPage);
        model.addAttribute("users", users);
        model.addAttribute("nums", users.size());
        model.addAttribute("hasNextPage", 1);
        return "user/user_lists";
    }

    @RequestMapping(value = "/user/user_search", method = RequestMethod.POST)
    public String getUser(
            @RequestParam("search-field") String type,
            @RequestParam("keyword") String value,
            Model model) {
        List<User> users = null;
        if (value.length() > 0){
            if ("byName".equals(type))
                users = userService.getUserByName(value);
            else if ("byId".equals(type))
                // 在前端保证输入的是整数
                users = userService.getUserById(Integer.parseInt(value));
            model.addAttribute("curPage", 1);
            model.addAttribute("users", users);
            model.addAttribute("nums", users.size());
            model.addAttribute("hasNextPage", 0);
        }
        return "user/user_lists";
    }

    @RequestMapping(value = "/pre-index2", method = RequestMethod.GET)
    public String toPreIndex2(
            HttpSession session,
            Model model) {
        return "pre-index2";
    }

    @RequestMapping(value = "/pre-index", method = RequestMethod.GET)
    public String toPreIndex(
            HttpSession session,
            Model model) {
        return "pre-index";
    }

}
