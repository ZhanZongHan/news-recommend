package com.zzh.news;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.spark.MongoSpark;
import com.zzh.news.run.NewsWeightRun;
import com.zzh.news.run.UserSimRecommendRun;
import com.zzh.news.run.UserWeightRun;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.BLAS;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.UserDefinedFunction;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeightCompute implements  Serializable{
    private static final long serialVersionUID = 5809782578272943999L;

    public static void main(String[] args) {
        // 指定日期
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String date = sdf.format(new Date());
        String date = "20200420";
        System.out.println(date);
        // 离线计算新闻权重
//        new NewsWeightRun(date).run();

        // 离线更新用户关键词权重，按照时间衰减


        // 离线更新计算新的一天的日志的用户关键词权重
//        new UserWeightRun(date).run();



        // 离线计算用户的推荐新闻
        new UserSimRecommendRun(date).run();


        // 仅限于当天爬取的新闻



        //
    }



    public <T> void saveIntoMongo(Dataset<T> data, String collection) {
        MongoSpark.write(data)
                .option("collection", collection)
                .mode("overwrite")
                .save();
    }
}
