package com.zzh.controller;

import com.zzh.dto.BrowsingLogDto;
import com.zzh.service.LogService;
import com.zzh.tools.MySqlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LogController {

    @Autowired
    private LogService logService;

    @RequestMapping("/log/log_index")
    public String getLogsByIndex(Model model) {
        List<BrowsingLogDto> logs = logService.getLogsByPage(1, MySqlProperties.LOG_PAGE_NUM);
        model.addAttribute("curPage", 1);
        model.addAttribute("logs", logs);
        model.addAttribute("nums", logs.size());
        model.addAttribute("hasNextPage", 1);
        return "log/log_lists";
    }

    @RequestMapping("/log/log_lists.html")
    public String getLogsByPage(
            @RequestParam("nextPage") Integer nextPage,
            Model model) {
        List<BrowsingLogDto> logs = logService.getLogsByPage(nextPage, MySqlProperties.LOG_PAGE_NUM);
        model.addAttribute("curPage", nextPage);
        model.addAttribute("logs", logs);
        model.addAttribute("nums", logs.size());
        model.addAttribute("hasNextPage", 1);
        return "log/log_lists";
    }

    @RequestMapping("/log/log_search")
    public String getLogsByUser(
                                  @RequestParam("search-field") String type,
                                  @RequestParam("keyword") String value,
                                  Model model) {
        List<BrowsingLogDto> logs = null;
        if (value.length() > 0) {
            if ("byName".equals(type))
                logs = logService.getLogsByUsername(value);
            else if ("byId".equals(type))
                // 在前端保证输入的是整数
                logs = logService.getLogsByUserId(Integer.parseInt(value));
        }
        model.addAttribute("curPage", 1);
        model.addAttribute("logs", logs);
        model.addAttribute("nums", logs.size());
        model.addAttribute("hasNextPage", 0);
        return "log/log_lists";
    }

    @RequestMapping("/pre/createALog")
    public void createALog(
            @RequestParam("userId") Integer userId,
            @RequestParam("newsId") Integer newsId
    ) {
        System.out.println(userId);
        System.out.println(newsId);
    }

}
