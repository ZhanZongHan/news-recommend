package com.zzh.news.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class UserSimilarityWithArray implements Serializable {
    private static final long serialVersionUID = 58097899L;
    private Integer userId;
    private ArrayList<Integer> RecommendedNewsIds;

    public UserSimilarityWithArray() {

    }

    public UserSimilarityWithArray(Integer userId, ArrayList<Integer> recommendedNewsIds) {
        this.userId = userId;
        RecommendedNewsIds = recommendedNewsIds;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ArrayList<Integer> getRecommendedNewsIds() {
        return RecommendedNewsIds;
    }

    public void setRecommendedNewsIds(ArrayList<Integer> recommendedNewsIds) {
        RecommendedNewsIds = recommendedNewsIds;
    }

    @Override
    public String toString() {
        return "UserSimilarityWithArray{" +
                "userId=" + userId +
                ", RecommendedNewsIds=" + RecommendedNewsIds +
                '}';
    }
}
