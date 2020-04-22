package com.zzh.news.run;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import com.zzh.news.bean.NewsSimilarity;
import com.zzh.news.bean.UserSimilarityWithArray;
import com.zzh.news.bean.UserWeight;
import com.zzh.news.utils.SparkUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSimRecommendRun implements Run, Serializable {
    private static final long serialVersionUID = 580943999L;

    private SparkUtils sparkUtils;
    private String userWeightCollection;
    private String newsWeightCollection;
    private String simCollection;


    public UserSimRecommendRun(String date) {
        sparkUtils = SparkUtils.getInstance();
        this.userWeightCollection = "user_weight";
        this.newsWeightCollection = "news_weight_" + date;
        this.simCollection = "user_sim_" + date;
    }

    @Override
    public void run() {
        JavaMongoRDD<Document> userWeights = MongoSpark.load(JavaSparkContext.fromSparkContext(sparkUtils.getSpark().sparkContext()),
                ReadConfig.create(sparkUtils.getSpark()).withOption("collection", userWeightCollection));

        JavaMongoRDD<Document> newsWeights = MongoSpark.load(JavaSparkContext.fromSparkContext(sparkUtils.getSpark().sparkContext()),
                ReadConfig.create(sparkUtils.getSpark()).withOption("collection", newsWeightCollection));
        Broadcast<List<Document>> newsesBroadcast =
                JavaSparkContext.fromSparkContext(sparkUtils.getSpark().sparkContext()).broadcast(newsWeights.collect());


        JavaRDD<UserSimilarityWithArray> userWithRecommendedNews = userWeights.map((Function<Document, UserSimilarityWithArray>) userWeight -> {
            // userWeight结构 Document<Integer, Array<Document<String, Integer, Date>>>
            // newses结构 Array<Document<Integer, Array<Document<String, Integer>>>>
            List<Document> newses = newsesBroadcast.getValue();
            Integer userId = userWeight.getInteger("user_id");
            ArrayList<Document> userWordWeights = userWeight.get("word_weight", ArrayList.class);

            ArrayList<NewsSimilarity> newsSimilarities = new ArrayList<>();
            double simValue;
            for (Document news : newses) {
                // news结构 Integer, Array<Document<String, Double>>
                Integer newsId = news.getInteger("news_id");

                ArrayList<Document> newsWordWeights = news.get("word_weight", ArrayList.class);
                simValue = 0;
                for (Document userWordWeight : userWordWeights) {
                    String userWord = userWordWeight.getString("word");
                    for (Document newsWordWeight : newsWordWeights) {
                        if (userWord.equals(newsWordWeight.getString("word"))) {
                            simValue += (userWordWeight.getDouble("weight") * newsWordWeight.getDouble("weight"));
                            break;
                        }
                    }
                }
                newsSimilarities.add(new NewsSimilarity(newsId, simValue));
            }
            newsSimilarities.sort((o1, o2) -> {
                Double v1 = o1.getSimValue();
                Double v2 = o2.getSimValue();
                if (v1 > v2)
                    return -1;
                else if (v1.equals(v2))
                    return 0;
                else
                    return 1;
            });
            ArrayList<Integer> recommendedNewsIds = new ArrayList<>();
            int cnt = 1;
            for (NewsSimilarity similarity : newsSimilarities) {
                // 只添加100篇新闻作为相似新闻
                recommendedNewsIds.add(similarity.getNewsId());
                if (cnt++ > 100)
                    break;
            }
            return new UserSimilarityWithArray(userId, recommendedNewsIds);
//            return new Document("userId", userId).append("recommendedNewsIds", recommendedNewsIds);
        });
//        MongoSpark.save(userWithRecommendedNews,
//                WriteConfig.create(sparkUtils.getSpark()).withOption("collection", simCollection),
//                Document.class);

        Dataset<Row> dataset = sparkUtils.getSpark().createDataFrame(userWithRecommendedNews, UserSimilarityWithArray.class);
        sparkUtils.saveIntoMongo(dataset, simCollection);
    }
}
