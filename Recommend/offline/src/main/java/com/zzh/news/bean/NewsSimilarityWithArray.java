package com.zzh.news.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class NewsSimilarityWithArray implements Serializable {
    private static final long serialVersionUID = 5809782578272943999L;

    private Integer newsId;
    private ArrayList<Integer> RecommendedNewsIds;

    public NewsSimilarityWithArray() {

    }

    public NewsSimilarityWithArray(Integer newsId, ArrayList<Integer> recommendedNewsIds) {
        this.newsId = newsId;
        RecommendedNewsIds = recommendedNewsIds;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public ArrayList<Integer> getRecommendedNewsIds() {
        return RecommendedNewsIds;
    }

    public void setRecommendedNewsIds(ArrayList<Integer> recommendedNewsIds) {
        RecommendedNewsIds = recommendedNewsIds;
    }
}
