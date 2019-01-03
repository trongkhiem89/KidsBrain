package com.kid.brain.provider.request.model.history;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class RatingVoResponse implements Serializable {

    @SerializedName("subCategoryRatingId")
    private String rateId;

    @SerializedName("score")
    private String score;

    @SerializedName("totalScore")
    private String totalScore;

    @SerializedName("recommendations")
    private String name;

    public RatingVoResponse() {
        this.score = "0";
        this.totalScore = "0";
    }

    public String getRateId() {
        return rateId;
    }

    public String getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String getTotalScore() {
        return totalScore;
    }

    @Override
    public String toString() {
        return "RatingVoResponse{" +
                "rateId='" + rateId + '\'' +
                ", score='" + score + '\'' +
                ", totalScore='" + totalScore + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
