package com.zzh.service;

import com.zzh.mapper.NewsMapper;
import com.zzh.mapper.UserMapper;
import com.zzh.mapper.mongodb.UserWeightDao;
import com.zzh.mapper.mongodb.UserWeightDaoImpl;
import com.zzh.pojo.NewsPart;
import com.zzh.pojo.NewsShowing;
import com.zzh.pojo.User;
import com.zzh.pojo.UserPush;
import com.zzh.pojo.mongo.UserSim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserWeightDaoImpl userWeightDao;
    @Autowired
    private NewsMapper newsMapper;

    public List<User> getUserById(Integer userId) {
        return userMapper.getUserById(userId);
    }

    public List<User> getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    public List<User> getUsersByPage(Integer start, Integer num) {
        return userMapper.getUsersByPage(start - 1, num);
    }

    public Integer getCount() {
        return userMapper.getCount();
    }

    public User isLegalUser(String username, String password) {
        List<User> users = getUserByName(username);
        User user = null;
        if (!users.isEmpty()) {
            user = users.get(0);
            if (user.getPassword().equals(password))
                return user;
        }
        return null;
    }

    public UserPush initLoginUser(User user, Integer newsStart, Integer newsNum) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String date = sdf.format(new Date());
        String date = "20200420";
        UserSim oneUserSim = userWeightDao.getOneUserSim(user.getUserId(), "user_sim_" + date);

        UserPush userPush = new UserPush();
        userPush.setUser(user);
        userPush.setNormalNews(newsMapper.getNewsShowingByPage(newsStart - 1, newsNum));

        if (oneUserSim == null) {
            // 表示当前用户没有日志记录
            userPush.setRecommendedNews(userPush.getNormalNews());
            return userPush;
        }

        ArrayList<Integer> recommendedNewsIds = oneUserSim.getRecommendedNewsIds();
        List<NewsShowing> recommendedNewses = null;
        if (recommendedNewsIds.size() > 0) {
            recommendedNewses = newsMapper.getNewsShowingById(recommendedNewsIds.get(0));
        } else {
            return null;
        }
        for (int i = 1; i < recommendedNewsIds.size(); i++) {
            recommendedNewses.add(newsMapper.getNewsShowingById(recommendedNewsIds.get(i)).get(0));
        }
        userPush.setRecommendedNews(recommendedNewses);

        return userPush;
    }
}
