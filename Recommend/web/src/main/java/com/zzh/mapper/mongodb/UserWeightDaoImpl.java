package com.zzh.mapper.mongodb;

import com.zzh.pojo.mongo.UserSim;
import com.zzh.pojo.mongo.UserWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class UserWeightDaoImpl implements UserWeightDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserWeight getOneUserWeight(Integer userId, String collection) {
        Query userWeightQuery = new Query(Criteria.where("user_id").is(userId));
        return mongoTemplate.findOne(userWeightQuery, UserWeight.class, collection);
    }

    @Override
    public UserSim getOneUserSim(Integer userId, String collection) {
        Query userSimQuery = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(userSimQuery, UserSim.class, collection);
    }
}
