package com.zzh.news.bean;

import java.util.Date;
import java.util.List;

public class UserWeight {
    private Integer userId;

    private List<UserWordWeight> wordWeight;

    public static class UserWordWeight{
        private String word;

        private Double weight;

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
            return "UserWeight{" +
                    "word='" + word + '\'' +
                    ", weight=" + weight +
                    ", browsingDate=" + browsingDate +
                    '}';
        }
    }
}
