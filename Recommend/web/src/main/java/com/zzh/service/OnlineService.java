package com.zzh.service;

import com.zzh.pojo.*;
import com.zzh.pojo.mongo.NewsWeight;
import com.zzh.pojo.mongo.UserWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OnlineService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void updateUserWeight(BrowsingLog log) {
        // 获取新闻权重，已按权值降序排序
        Query newsWeightQuery = new Query(Criteria.where("news_id").is(log.getNewsId()));
        NewsWeight newsWeight = mongoTemplate.findOne(newsWeightQuery, NewsWeight.class);
        if (newsWeight == null)
            return;

        List<NewsWeight.NewsWordWeight> newsWordWeights = newsWeight.getWordWeight();

        // 获取用户权重，并判断是否存在
        Query userWeightQuery = new Query(Criteria.where("user_id").is(log.getUserId()));
        UserWeight userWeight = mongoTemplate.findOne(userWeightQuery, UserWeight.class);

        if (userWeight == null) {
            // 不存在就添加
            UserWeight userWeightAnother = new UserWeight();
            userWeightAnother.setUserId(log.getUserId());

            List<UserWeight.UserWordWeight> userWordWeights = new ArrayList<>(newsWordWeights.size());

            for (int i = 0; i < newsWordWeights.size(); i++) {
                NewsWeight.NewsWordWeight newsWordWeight = newsWordWeights.get(i);

                // 构造新对象
                UserWeight.UserWordWeight userWordWeight = new UserWeight.UserWordWeight(
                        newsWordWeight.getWord(),
                        newsWordWeight.getWeight(),
                        log.getBrowsingDate()
                );

                userWordWeights.add(userWordWeight);
            }
            userWeightAnother.setWordWeight(userWordWeights);

            mongoTemplate.insert(userWeightAnother);

        } else {
            // 存在就更新
            List<UserWeight.UserWordWeight> userWordWeights = userWeight.getWordWeight();
            // 判断关键词是否有交集
            boolean isIn = false;
            for (NewsWeight.NewsWordWeight newsWordWeight : newsWordWeights) {
                for (UserWeight.UserWordWeight userWordWeight : userWordWeights) {
                    if (userWordWeight.getWord().equals(newsWordWeight.getWord())) {
                        // 累加更新
                        userWordWeight.setWeight(userWordWeight.getWeight() + newsWordWeight.getWeight());
                        // 更细浏览日期
                        userWordWeight.setBrowsingDate(log.getBrowsingDate());
                        isIn = true;
                        break;
                    }
                }
                if (!isIn) {
                    // 构造新对象
                    UserWeight.UserWordWeight userWordWeight = new UserWeight.UserWordWeight(
                            newsWordWeight.getWord(),
                            newsWordWeight.getWeight(),
                            log.getBrowsingDate()
                    );
                    userWordWeights.add(userWordWeight);
                }

            }
            Update update = new Update();
            update.set("word_weight", userWordWeights);
            mongoTemplate.updateFirst(userWeightQuery, update, UserWeight.class);
        }
    }

    public List<NewsShowing> recommend(User user) {
        // 获取


        return null;
    }
}
