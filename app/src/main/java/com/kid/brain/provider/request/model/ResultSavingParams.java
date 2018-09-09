package com.kid.brain.provider.request.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 4/5/17.
 *
 * {
     "lstSubCategoryIds": [
        0
     ],
     "lstSubCategoryRatingIds": [
         {
         "score": 0,
         "subCategoryRatingId": 0
         }
     ]
 }
 */

public class ResultSavingParams implements Serializable {

    @SerializedName("lstSubCategoryIds")
    private List<Integer> questionIds;

    @SerializedName("lstSubCategoryRatingIds")
    private List<ScoreRate> ratingIds;

    public List<Integer> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<Integer> questionIds) {
        this.questionIds = questionIds;
    }

    public List<ScoreRate> getRatingIds() {
        return ratingIds;
    }

    public void setRatingIds(List<ScoreRate> ratingIds) {
        this.ratingIds = ratingIds;
    }
}
