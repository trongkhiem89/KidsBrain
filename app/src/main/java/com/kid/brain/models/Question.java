package com.kid.brain.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khiemnt on 4/17/17.
 */

public class Question implements Serializable {

    @SerializedName("subCategoryId")
    private int id;

    @SerializedName("cateId")
    private int cateId;

    @SerializedName("ageId")
    private int levelId;

    @SerializedName("content")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("check")
    private boolean isChecked;


    public int getCateId() {
        return cateId;
    }

    public void setCateId(int cateId) {
        this.cateId = cateId;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Id:" + getId() +"\n"
                + "Name:" + getName() + "\n"
                + "Level Id:" + getLevelId() + "\n"
                + "Cate Id:" + getCateId() + "\n"
                + "Description:" + getDescription() + "\n"
                + "Status:" + getStatus() + "\n"
                + "isChecked:" + isChecked();
    }
}
