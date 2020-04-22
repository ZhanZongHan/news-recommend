package com.zzh.controller;

import com.zzh.pojo.NewsPart;
import com.zzh.pojo.NewsShowing;
import com.zzh.pojo.UserPush;
import com.zzh.service.EsService;
import com.zzh.service.NewsService;
import com.zzh.tools.MySqlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;
    @Autowired
    private EsService esService;

    @RequestMapping("/news/news_index")
    public String getNewsPartByIndex(Model model) {
        List<NewsPart> newses = newsService.getNewsPartByPage(1, MySqlProperties.NEWS_PAGE_NUM);
        model.addAttribute("curPage", 1);
        model.addAttribute("newses", newses);
        model.addAttribute("nums", newses.size());
        model.addAttribute("hasNextPage", 1);
        return "news/news_lists";
    }

    @RequestMapping("/news/news_lists.html")
    public String getUsersByPage(
            @RequestParam("nextPage") Integer nextPage,
            Model model) {
        List<NewsPart> newses = newsService.getNewsPartByPage(nextPage, MySqlProperties.NEWS_PAGE_NUM);
        model.addAttribute("curPage", nextPage);
        model.addAttribute("newses", newses);
        model.addAttribute("nums", newses.size());
        model.addAttribute("hasNextPage", 1);
        return "news/news_lists";
    }

    @RequestMapping(value = "/news/news_search", method = RequestMethod.POST)
    public String searchNewsPart(
            @RequestParam("search-field") String type,
            @RequestParam("keyword") String value,
            Model model) {
        List<NewsPart> newsParts = null;
        if (value.length() > 0){
            if ("byName".equals(type)) {
                try {
                    newsParts = esService.searchTitle(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if ("byId".equals(type))
                // 在前端保证输入的是整数
                newsParts = newsService.getNewsPartById(Integer.parseInt(value));
            model.addAttribute("curPage", 1);
            model.addAttribute("newses", newsParts);
            model.addAttribute("nums", newsParts.size());
            model.addAttribute("hasNextPage", 0);
        }
        return "news/news_lists";
    }

    @RequestMapping(value = "/pre/news_search")
    public String searchNewsShowing(
            @RequestParam("keyword") String value,
            HttpSession session,
            Model model) {
        List<NewsShowing> newsShowings = null;
        try {
            newsShowings = esService.searchTitleAndContent(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("curPage", 1);
        model.addAttribute("newses", newsShowings);
        model.addAttribute("nums", newsShowings.size());
        model.addAttribute("hasNextPage", 0);
        return "pre-index2";
    }
}
