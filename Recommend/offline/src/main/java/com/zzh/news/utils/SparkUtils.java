package com.zzh.news.utils;

import com.mongodb.spark.MongoSpark;
import com.zzh.news.config.DBConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SparkUtils implements Serializable {
    private static final long serialVersionUID = 5809787827212943999L;
    private static volatile SparkUtils sparkUtils;
    private SparkSession spark;

    private SparkUtils() {
        //47.112.103.152
        SparkConf conf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("recommend")
                .set("spark.mongodb.input.uri", "mongodb://" + DBConfig.MONGO_HOST + ":" + DBConfig.MONGO_PORT + "/" + DBConfig.MONGO_DB_NAME + ".spark_test")
                .set("spark.mongodb.output.uri", "mongodb://" + DBConfig.MONGO_HOST + ":" + DBConfig.MONGO_PORT + "/" + DBConfig.MONGO_DB_NAME + ".spark_test");
        spark = SparkSession.builder()
                .config(conf)
                .getOrCreate();
    }

    public Dataset<Row> getData(String tableName) {
        Map<String, String> map = new HashMap<>();
        map.put("url", "jdbc:mysql://" + DBConfig.MYSQL_DB_NAME + ":" + DBConfig.MYSQL_PORT + "/news?useUnicode=true&characterEncoding=gbk");
        map.put("driver", "com.mysql.jdbc.Driver");
        map.put("dbtable", DBConfig.MYSQL_DB_NAME + "." + tableName);
        map.put("user", DBConfig.MYSQL_USERNAME);
        map.put("password", DBConfig.MYSQL_PASSWORD);
        return spark.read()
                .format("jdbc")
                .options(map)
                .load();
    }

    public static SparkUtils getInstance() {
        if (sparkUtils == null) {
            synchronized (SparkUtils.class) {
                if (sparkUtils == null) {
                    sparkUtils = new SparkUtils();
                }
            }
        }
        return sparkUtils;
    }

    public SparkSession getSpark() {
        return spark;
    }

    public <T> void saveIntoMongo(Dataset<T> data, String collection) {
        MongoSpark.write(data)
                .option("collection", collection)
                .mode("overwrite")
                .save();
    }

    public void close() {
        spark.close();
    }
}
