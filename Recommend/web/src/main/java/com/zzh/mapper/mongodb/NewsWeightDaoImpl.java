package com.zzh.mapper.mongodb;

import com.zzh.pojo.mongo.NewsSim;
import com.zzh.pojo.mongo.NewsWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsWeightDaoImpl implements NewsWeightDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public NewsWeight getOneNewsWeight(Integer newsId, String collection) {
        Query newsWeightQuery = new Query(Criteria.where("news_id").is(newsId));
        return mongoTemplate.findOne(newsWeightQuery, NewsWeight.class, collection);

    }

    @Override
    public NewsSim getOneNewsSim(Integer newsId, String collection) {
        Query newsSimQuery = new Query(Criteria.where("newsId").is(newsId));
        return mongoTemplate.findOne(newsSimQuery, NewsSim.class, collection);
    }
}
