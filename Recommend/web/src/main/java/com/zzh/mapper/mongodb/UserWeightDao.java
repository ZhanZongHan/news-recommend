package com.zzh.mapper.mongodb;

import com.zzh.pojo.mongo.UserSim;
import com.zzh.pojo.mongo.UserWeight;

public interface UserWeightDao {
    public UserWeight getOneUserWeight(Integer userId, String collection);

    public UserSim getOneUserSim(Integer userId, String collection);
}
