package com.kid.brain.models;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class QuestionScore implements Serializable {

    private Category category;
    private Kid level;
    private String rateId;
    private int score;
    private String questionIds;

    public QuestionScore() {
        this.rateId = "0";
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Kid getLevel() {
        return level;
    }

    public void setLevel(Kid level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(String questionIds) {
        this.questionIds = questionIds;
    }

    @Override
    public String toString() {
        return "QuestionScore{" +
                "category=" + category +
                ", level=" + level +
                ", rateId=" + rateId +
                ", score=" + score +
                ", questionIds=" + questionIds +
                '}';
    }
}
