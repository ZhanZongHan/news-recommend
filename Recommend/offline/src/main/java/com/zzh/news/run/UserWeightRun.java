package com.zzh.news.run;


import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.zzh.news.config.DBConfig;
import com.zzh.news.utils.SparkUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.*;

/*
    计算并存储用户关键词矩阵和用户推荐新闻矩阵
 */
public class UserWeightRun implements Run, Serializable {
    private static final long serialVersionUID = 58097878272943999L;
    private SparkUtils sparkUtils;
    private String userWeightCollection;
    private String newsWeightCollection;
    private String logTableName;

    public UserWeightRun(String date) {
        sparkUtils = SparkUtils.getInstance();
        this.logTableName = "log_info_" + date;
        this.userWeightCollection = "user_weight";
        this.newsWeightCollection = "news_weight_" + date;
    }

    @Override
    public void run() {

        //离线计算新闻关键词权重并返回
        // 出于内存的考虑，还是不用加载整个新闻集了，而是一条一条的从mongo里读取
//
        Dataset<Row> logData = sparkUtils.getData(logTableName);

        logData.foreachPartition((ForeachPartitionFunction<Row>) iterator -> {
            MongoClient client = new MongoClient(DBConfig.MONGO_HOST, DBConfig.MONGO_PORT);
            MongoDatabase db = client.getDatabase(DBConfig.MONGO_DB_NAME);
            while (iterator.hasNext()) {
                Row row = iterator.next();
                int userId = row.getInt(1);
                int newsId = row.getInt(2);
                Date browsingDate = row.getAs(3);

                Document oneNewsWeight =
                        getOneItem(db, newsWeightCollection, "news_id", newsId);
                if (oneNewsWeight == null)
                    continue;
                ArrayList<Document> newsWordWeights = oneNewsWeight.get("word_weight", ArrayList.class);



                Document oneUserWeight = getOneItem(db, userWeightCollection, "user_id", userId);
                if (oneUserWeight == null) {
                    ArrayList<Document> documents = new ArrayList<>();
                    for (Document newsWordWeight : newsWordWeights) {
                        documents.add(
                                new Document("word", newsWordWeight.getString("word"))
                                        .append("weight", newsWordWeight.getDouble("weight"))
                                        .append("browsing_date", browsingDate)
                        );

                    }
                    // 将用户的关键词数限定在100以下
                    List<Document> newDocuments = judgeDocuments(documents);

                    insertOneItem(
                            db,
                            userWeightCollection,
                            new Document("user_id", userId).append("word_weight", newDocuments)
                    );
                } else {
                    // 更新用户的关键词权重
                    ArrayList<Document> userWordWeights = oneUserWeight.get("word_weight", ArrayList.class);

                    // 获取用户原先就存在的关键词
                    ArrayList<String> userWords = new ArrayList<>(userWordWeights.size());
                    for (Document userWordWeight : userWordWeights) {
                        userWords.add(userWordWeight.getString("word"));
                    }



                    boolean isIn;
                    for (Document newsWordWeight : newsWordWeights) {
                        isIn = false;
                        for (int i = 0; i < userWords.size(); i++) {
                            if (newsWordWeight.getString("word")
                                    .equals(userWords.get(i))) {
                                // 词语存在用户中就累加权重
                                Document userWordWeightDoc = userWordWeights.get(i);
                                userWordWeightDoc.put("weight",
                                        userWordWeightDoc.getDouble("weight") + newsWordWeight.getDouble("weight"));

                                isIn = true;
                                break;
                            }
                        }
                        if (!isIn) {
                            // 若这个词语在用户中没有，就添加
                            userWordWeights.add(
                                    new Document("word", newsWordWeight.getString("word"))
                                            .append("weight", newsWordWeight.getDouble("weight"))
                                            .append("browsing_date", browsingDate)
                            );
                        }
                    }
                    // 将用户的关键词数限定在100以下
                    List<Document> newDocuments = judgeDocuments(userWordWeights);

                    updateOneItem(db,
                            userWeightCollection,
                            "user_id",
                            userId,
                            new Document("$set", new Document("word_weight", newDocuments)));
                }
            }
            client.close();
        });

    }

    public Document getOneItem(MongoDatabase database, String collection, String conditionCol, Integer value) {
        Bson filter = Filters.eq(conditionCol, value);
        FindIterable<Document> values = database.getCollection(collection).find(filter);
        return values.first();
    }

    public void insertOneItem(MongoDatabase database, String collection, Document doc) {
        database.getCollection(collection).insertOne(doc);
    }

    public void updateOneItem(MongoDatabase database, String collection, String conditionCol, Integer value, Document doc) {

        database.getCollection(collection).updateOne(
                Filters.eq(conditionCol, value),
                doc
        );
    }

    public List<Document> judgeDocuments(List<Document> documents) {
        if (documents.size() >= 100) {
            documents.sort((o1, o2) -> {
                Double v1 = o1.getDouble("weight");
                Double v2 = o2.getDouble("weight");
                if (v1 - v2 > 0)
                    return -1;
                else if (v1.equals(v2))
                    return 0;
                else
                    return 1;
            });
            return documents.subList(0, 100);
        }
        return documents;
    }

}
