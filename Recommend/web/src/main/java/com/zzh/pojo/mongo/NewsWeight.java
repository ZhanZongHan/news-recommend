package com.zzh.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "news_weight")
public class NewsWeight {
    private static final long serialVersionUID = -3258839839160856613L;

    @Field("news_id")
    private Integer newsId;

    @Field("word_weight")
    private List<NewsWordWeight> wordWeight;

    public static class NewsWordWeight {
        @Field("word")
        private String word;

        @Field("weight")
        private Double weight;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "WordWeight{" +
                    "word='" + word + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}


