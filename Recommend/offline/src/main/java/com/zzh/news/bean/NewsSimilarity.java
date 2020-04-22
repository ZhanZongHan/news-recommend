package com.zzh.news.bean;

import java.io.Serializable;

public class NewsSimilarity implements Serializable {
    private static final long serialVersionUID = 5809782578272943999L;

    private Integer newsId;
    private Double simValue;

    public NewsSimilarity() {

    }

    public NewsSimilarity(Integer newsId, Double simValue) {
        this.newsId = newsId;
        this.simValue = simValue;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public Double getSimValue() {
        return simValue;
    }

    public void setSimValue(Double simValue) {
        this.simValue = simValue;
    }
}
