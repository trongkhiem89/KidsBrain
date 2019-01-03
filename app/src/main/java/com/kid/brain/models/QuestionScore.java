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
    private int totalScore;
    private String questionIds;

    public QuestionScore() {
        this.rateId = "0";
        this.score = 0;
        this.totalScore = 0;
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

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
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
                "category=" + category.getId() +
                ", level=" + level.getAgeId() +
                ", rateId=" + rateId +
                ", score=" + score +
                ", totalScore=" + totalScore +
                ", questionIds=" + questionIds +
                '}';
    }
}
