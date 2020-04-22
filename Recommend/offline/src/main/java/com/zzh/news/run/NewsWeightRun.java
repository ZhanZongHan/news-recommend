package com.zzh.news.run;

import com.zzh.news.bean.NewsSimilarity;
import com.zzh.news.bean.NewsSimilarityWithArray;
import com.zzh.news.utils.UdfUtils;
import com.zzh.news.WeightCompute;
import com.zzh.news.utils.SparkUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.BLAS;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.UserDefinedFunction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/*
    计算并存储了新闻关键词矩阵和新闻相似矩阵
 */
public class NewsWeightRun implements Run, Serializable {
    private static final long serialVersionUID = 58097878272943999L;
    private SparkUtils sparkUtils;
    private String tableName;
    private String weightCollection;
    private String simCollection;

    public NewsWeightRun(String date) {
        sparkUtils = SparkUtils.getInstance();
        this.tableName = "news_info_" + date;
        this.weightCollection = "news_weight_" + date;
        this.simCollection = "news_sim_" + date;
    }

    @Override
    public void run() {

        // tableName "news_small"
        // spark与mysql建立连接，读取数据创建rdd
        // 读取当天的新闻计算新闻矩阵

        Dataset<Row> data = sparkUtils.getData(tableName);
        data = data.select("news_id", "title", "content");


        // 数据预处理
        data = dataPreHandle(data);

        // 分词
        data = segment(data);

        // 获取停用词表
        String[] stopWords = getStopWords();

        // 去除停用词
        data = removeStopWord(data, stopWords);

        // 得到tfidf rdd 并计算新闻相似度矩阵
        data = getNewsWeight(data);

        // 存入mongodb
        System.out.println("正在存储新闻关键词矩阵。。。。。");
        sparkUtils.saveIntoMongo(data, weightCollection);
        data.unpersist();
        // 表结构 Integer List<Row>
        // Row: String, Double
    }

    public Dataset<Row> dataPreHandle(Dataset<Row> data) {
        // 过滤空数据
        data = data.where(data.col("content").notEqual("")
                .and(data.col("title").notEqual("")));

        // 合并标题和内容，并加大标题权重
        UserDefinedFunction merge = UdfUtils.mergeTitleAndContent();
        data = data.withColumn("all_content",
                merge.apply(data.col("title"), data.col("content")));
        // 去除不需要的列
        return data.drop("title", "content");
    }

    public Dataset<Row> segment(Dataset<Row> data) {
        UserDefinedFunction seg = UdfUtils.segUdf();
        data = data.withColumn("all_words", seg.apply(data.col("all_content")));
        return data.drop("all_content");
    }

    public String[] getStopWords() {
        ArrayList<String> stopWords = new ArrayList<>();
        InputStream words =
                WeightCompute.class.getClassLoader().getResourceAsStream("stop_words.txt");

        BufferedReader bis = null;
        String line;
        try {
            bis = new BufferedReader(new InputStreamReader(words));
            while ((line = bis.readLine()) != null) {
                stopWords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stopWords.toArray(new String[0]);
    }

    public Dataset<Row> removeStopWord(Dataset<Row> data, String[] stopWords) {
        StopWordsRemover remover = new StopWordsRemover()
                .setInputCol("all_words")
                .setOutputCol("words")
                .setStopWords(stopWords);
        return remover.transform(data).drop("all_words");
    }

    public Dataset<Row> getNewsWeight(Dataset<Row> data) {
        // 计算tfidf
        // 计算tf值
        CountVectorizerModel cvModel = new CountVectorizer()
                .setInputCol("words")
                .setOutputCol("tf")
                .setVocabSize(2 << 20).fit(data);

        Dataset<Row> cvTransform = cvModel.transform(data).drop("words");
        String[] vocabulary = cvModel.vocabulary();

        Broadcast<String[]> vocabBroadcast
                = JavaSparkContext.fromSparkContext(sparkUtils.getSpark().sparkContext()).broadcast(vocabulary);
        //
        IDF idf = new IDF()
                .setInputCol("tf")
                .setOutputCol("tfidf_value");
        IDFModel idfModel = idf.fit(cvTransform);

        // (262144,[7428,451...  tfidf_value值的形式，是个SparseVector
        // 262144是size(),[]，里面的值是不为0的位置
        // indices属性是不为零的索引，values属性是不为零的值
        Dataset<Row> rescaleData = idfModel.transform(cvTransform).drop("tf");
        rescaleData = rescaleData.cache();

        // 计算新闻相似度矩阵
        Dataset<NewsSimilarityWithArray> newsSimilarityMatrix = getNewsSimilarityMatrix(rescaleData);
        System.out.println("正在存储相似新闻矩阵。。。。。");
        sparkUtils.saveIntoMongo(newsSimilarityMatrix, simCollection);


        UserDefinedFunction vectorTransformUdf = UdfUtils.vectorTransform(vocabBroadcast);
        rescaleData = rescaleData.withColumn("word_weight"
                , vectorTransformUdf.apply(rescaleData.col("tfidf_value")));

        return rescaleData.drop("tfidf_value");
    }

    public Dataset<NewsSimilarityWithArray> getNewsSimilarityMatrix(Dataset<Row> data) {
        // 先对cache()的rdd执行一次行动算子才会cache生效
        Broadcast<List<Row>> newsBroadcast =
                JavaSparkContext.fromSparkContext(sparkUtils.getSpark().sparkContext()).broadcast(data.collectAsList());

        Dataset<NewsSimilarityWithArray> res = data.map((MapFunction<Row, NewsSimilarityWithArray>) row -> {
            List<Row> values = newsBroadcast.getValue();
            ArrayList<Integer> recommendedNewsIds = new ArrayList<>();
            List<NewsSimilarity> similarities = new ArrayList<>();
            // 计算当前新闻与所有新闻的相似度
            for (Row value : values) {
                Vector vec1 = row.getAs(1);
                Vector vec2 = value.getAs(1);

                double dot = BLAS.dot(vec1.toSparse(), vec2.toSparse());
                double v1 = Vectors.norm(vec1.toSparse(), 2);
                double v2 = Vectors.norm(vec2.toSparse(), 2);
                double sim = dot / (v1 * v2);
                similarities.add(new NewsSimilarity(value.getInt(0), sim));
            }
            similarities.sort((o1, o2) -> {
                if (o1.getSimValue() > o2.getSimValue())
                    return -1;
                else if (o1.getSimValue().equals(o2.getSimValue()))
                    return 0;
                else
                    return 1;
            });
            int cnt = 1;
            for (NewsSimilarity similarity : similarities) {
                // 只添加100篇新闻作为相似新闻
                if (similarity.getSimValue() < 0.99) {
                    recommendedNewsIds.add(similarity.getNewsId());
                    if (cnt++ > 100)
                        break;
                }
            }

            return new NewsSimilarityWithArray(row.getInt(0), recommendedNewsIds);
        }, Encoders.bean(NewsSimilarityWithArray.class));
        return res;
    }
}
