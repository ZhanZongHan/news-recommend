package com.zzh.mapper.mongodb;

import com.zzh.pojo.mongo.NewsSim;
import com.zzh.pojo.mongo.NewsWeight;

public interface NewsWeightDao {
    public NewsWeight getOneNewsWeight(Integer newsId, String collection);

    public NewsSim getOneNewsSim(Integer newsId, String collection);
}
