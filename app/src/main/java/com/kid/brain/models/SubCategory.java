package com.kid.brain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */

public class SubCategory implements Serializable {

    @SerializedName("categoryMappingId")
    private int id;

    @SerializedName("ageId")
    private int levelId;

    @SerializedName("ageName")
    private String levelName;

    @SerializedName("categoryId")
    private int cateId;

    @SerializedName("categoryName")
    private String cateName;

    @SerializedName("linkImgCategory")
    private String cateImage;

//    @SerializedName("ageVO")
//    private Level level;
//
//    @SerializedName("categoryVO")
//    private Category category;

    @SerializedName("subCategoryVOs")
    private List<Question> questions;

    @SerializedName("subCategoryRatingVOs")
    private List<Rate> rates;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevelId() {
        return levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public int getCateId() {
        return cateId;
    }

    public String getCateName() {
        return cateName;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public String getCateImage() {
        return cateImage;
    }

    public void setCateImage(String cateImage) {
        this.cateImage = cateImage;
    }
}
