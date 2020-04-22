package com.zzh.news.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.spark.Partition;
import org.apache.spark.TaskContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.linalg.BLAS;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.expressions.UserDefinedFunction;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import scala.collection.Iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UdfUtils {
    public static UserDefinedFunction mergeTitleAndContent() {
        return functions.udf((String title, String content) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                sb.append(title).append(" ");
            }
            return sb.append(content).toString();
        }, DataTypes.StringType);
    }

    public static UserDefinedFunction segUdf() {
        return functions.udf((String s) -> {
            List<Term> pairs = HanLP.segment(s);
            List<String> words = new ArrayList<>();
            for (Term pair : pairs) {
                words.add(pair.word);
            }
            return words;
        }, DataTypes.createArrayType(DataTypes.StringType));
    }

    public static UserDefinedFunction vectorTransform(Broadcast<String[]> vocabBroadcast) {
        StructField[] fields = new StructField[]{
                DataTypes.createStructField("word", DataTypes.StringType, true),
                DataTypes.createStructField("weight", DataTypes.DoubleType, true)
        };
        return functions.udf((SparseVector vector) -> {

            int[] indices = vector.indices();
            double[] values = vector.values();
            String[] vocabulary = vocabBroadcast.getValue();
            List<Row> res = new ArrayList<>();

            for (int i  = 0; i < indices.length; i ++) {
                res.add(RowFactory.create(vocabulary[indices[i]], values[i]));
            }
            res.sort((o1, o2) -> {
                double a = o1.getDouble(1);
                double b = o2.getDouble(1);
                if (a - b > 0)
                    return -1;
                else if (a - b == 0)
                    return 0;
                else
                    return 1;
            });
            // 拿一半的关键字做新闻的标识
            return res.subList(0, res.size() / 4);
        }, DataTypes.createArrayType(DataTypes.createStructType(fields)));
    }
}
