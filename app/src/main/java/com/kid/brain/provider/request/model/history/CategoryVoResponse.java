package com.kid.brain.provider.request.model.history;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/5/17.
 */

public class CategoryVoResponse implements Serializable {

    @SerializedName("categoryId")
    private String cateId;

    @SerializedName("categoryName")
    private String cateName;

    @SerializedName("subCategoryRatingVO")
    private RatingVoResponse rate;

    public String getCateId() {
        return cateId;
    }

    public String getCateName() {
        return cateName;
    }

    public RatingVoResponse getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "CategoryVoResponse{" +
                "cateId='" + cateId + '\'' +
                ", cateName='" + cateName + '\'' +
                ", rate=" + rate +
                '}';
    }
}
