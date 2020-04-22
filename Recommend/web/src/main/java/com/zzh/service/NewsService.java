package com.zzh.service;

import com.zzh.mapper.NewsMapper;
import com.zzh.pojo.NewsPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<NewsPart> getNewsPartByPage(Integer start, Integer num) {
        return newsMapper.getNewsPartByPage(start - 1, num);
    }

    public List<NewsPart> getNewsPartById(Integer newsId) {
        return newsMapper.getNewsPartById(newsId);
    }

    public List<Integer> getRecommendedNewsIds(Integer userId) {
        Query newsWeightQuery = new Query(Criteria.where("user_id").is(userId));
        Document document = mongoTemplate.findOne(newsWeightQuery, Document.class, "");
        return null;
    }

}
