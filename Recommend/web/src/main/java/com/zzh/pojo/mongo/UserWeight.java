package com.zzh.pojo.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_weight")
public class UserWeight {
    private static final long serialVersionUID = -32588398391856613L;

    @Field("user_id")
    private Integer userId;

    @Field("word_weight")
    private List<UserWordWeight> wordWeight;

    public static class UserWordWeight{
        @Field("word")
        private String word;

        @Field("weight")
        private Double weight;

        @Field("browsing_date")
        private Date browsingDate;

        public UserWordWeight() {}

        public UserWordWeight(String word, Double weight, Date browsingDate) {
            this.word = word;
            this.weight = weight;
            this.browsingDate = browsingDate;
        }

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

        public Date getBrowsingDate() {
            return browsingDate;
        }

        public void setBrowsingDate(Date browsingDate) {
            this.browsingDate = browsingDate;
        }

        @Override
        public String toString() {
            return "UserWordWeight{" +
                    "word='" + word + '\'' +
                    ", weight=" + weight +
                    ", browsingDate=" + browsingDate +
                    '}';
        }
    }
}

