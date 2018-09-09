package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 11/7/16.
 */
public class ScoreRate implements Serializable {

    @SerializedName("score")
    private int score;

    @SerializedName("subCategoryRatingId")
    private int rateId;

    public ScoreRate(int score, int rateId) {
        this.score = score;
        this.rateId = rateId;
    }
}
