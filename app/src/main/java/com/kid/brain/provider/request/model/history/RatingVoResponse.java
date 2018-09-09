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

    @SerializedName("recommendations")
    private String name;

    public String getRateId() {
        return rateId;
    }

    public String getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "RatingVoResponse{" +
                "rateId='" + rateId + '\'' +
                ", score='" + score + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
