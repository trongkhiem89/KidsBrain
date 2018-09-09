package com.kid.brain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Rate implements Serializable {

    @SerializedName("subCategoryRatingId")
    private String id;

    @SerializedName("cateId")
    private String cateId;

    @SerializedName("ageId")
    private String levelId;

    @SerializedName("score")
    private String score;

    @SerializedName("rating")
    private String rating;

    @SerializedName("recommendations")
    private String name;

    public Rate(){
        id = "0";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "id='" + id + '\'' +
                ", cateId=" + cateId +
                ", levelId=" + levelId +
                ", score='" + score + '\'' +
                ", rating='" + rating + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
